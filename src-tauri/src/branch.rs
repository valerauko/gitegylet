use serde::Serialize;

#[derive(Serialize)]
pub struct Branch {
    name: String,
    commit: String,
    refname: String,
    is_head: bool,
    ahead_behind: (usize, usize),
}

impl Branch {
    pub fn from_git2(repo: &git2::Repository, branch: git2::Branch) -> Result<Self, git2::Error> {
        let name = match branch.name() {
            Ok(Some(name)) => name.to_string(),
            Ok(None) => return Err(git2::Error::from_str("Invalid branch name")),
            Err(e) => return Err(e),
        };
        let commit_id = branch.get().peel_to_commit()?.id();

        let ahead_behind = match branch.upstream() {
            Ok(upstream) => match upstream.get().peel_to_commit() {
                Ok(commit) => match repo.graph_ahead_behind(commit_id, commit.id()) {
                    Ok(ab) => ab,
                    Err(e) => {
                        println!("{}", e);
                        (0, 0)
                    }
                },
                Err(e) => {
                    println!("{}", e);
                    (0, 0)
                }
            },
            Err(e) => {
                println!("{}", e);
                (0, 0)
            }
        };

        let refname = match branch.get().name() {
            Some(name) => name.to_string(),
            None => return Err(git2::Error::from_str("Invalid branch refname")),
        };

        Ok(Self {
            commit: commit_id.to_string(),
            name,
            refname,
            is_head: branch.is_head(),
            ahead_behind,
        })
    }

    pub fn by_name(repo: &git2::Repository, name: &str) -> Result<Self, String> {
        let branch = repo.find_branch(name, git2::BranchType::Local).unwrap();
        match Self::from_git2(repo, branch) {
            Ok(branch) => Ok(branch),
            Err(e) => Err(e.message().to_string())
        }
    }

    pub fn locals(repo: &git2::Repository) -> Vec<Self> {
        let branches = repo.branches(Some(git2::BranchType::Local)).unwrap();
        branches.fold(vec![], |mut aggr, branch| match branch {
            Ok((branch, _type)) => match Branch::from_git2(&repo, branch) {
                Ok(b) => {
                    aggr.push(b);
                    aggr
                }
                Err(e) => {
                    println!("{}", e);
                    aggr
                }
            },
            Err(e) => {
                println!("{}", e);
                aggr
            }
        })
    }

    pub fn checkout(self, repo: &git2::Repository) -> Result<Self, String> {
        match repo.set_head(&self.refname) {
            Ok(_) => {
                let mut options = git2::build::CheckoutBuilder::default();
                options.force();
                match repo.checkout_head(Some(&mut options)) {
                    Ok(_) => Ok(self),
                    Err(e) => Err(e.message().to_string()),
                }
            }
            Err(e) => Err(e.message().to_string()),
        }
    }
}