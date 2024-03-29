use serde::Serialize;

#[derive(Clone, Debug, Serialize)]
pub struct Status {
    file: String,
    status: String,
}

impl Status {
    pub fn from_strings(file: String, status: String) -> Self {
        Self { file, status }
    }

    pub fn all(repo: &git2::Repository) -> Vec<Self> {
        let mut opts = git2::StatusOptions::new();
        opts.include_ignored(false)
            .include_untracked(true)
            .recurse_untracked_dirs(true);

        repo.statuses(Some(&mut opts))
            .unwrap()
            .iter()
            .map(|entry| {
                let status = match entry.status() {
                    s if s.contains(git2::Status::WT_NEW) => "new",
                    s if s.contains(git2::Status::WT_MODIFIED) => "modified",
                    s if s.contains(git2::Status::WT_DELETED) => "deleted",
                    s if s.contains(git2::Status::WT_RENAMED) => "renamed",
                    s if s.contains(git2::Status::INDEX_NEW) => "new",
                    s if s.contains(git2::Status::INDEX_MODIFIED) => "modified",
                    s if s.contains(git2::Status::INDEX_DELETED) => "deleted",
                    s if s.contains(git2::Status::INDEX_RENAMED) => "renamed",
                    s if s.contains(git2::Status::CONFLICTED) => "conflicted",
                    _ => "other",
                }
                .to_string();

                Self {
                    file: entry.path().unwrap().to_string(),
                    status,
                }
            })
            .collect()
    }
}
