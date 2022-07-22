#![cfg_attr(
  all(not(debug_assertions), target_os = "windows"),
  windows_subsystem = "windows"
)]

mod branch;
mod status;
mod commit;

use branch::Branch;
use status::Status;
use commit::Commit;

use git2::Repository;
use tauri::State;
use std::sync::Mutex;

#[tauri::command]
async fn branch_locals(state: State<'_, Mutex<Option<Repository>>>) -> Result<Vec<Branch>, String> {
  match &*state.inner().lock().expect("Could not lock mutex") {
    Some(repo) => Ok(Branch::locals(repo)),
    None => Err("No open repo".to_string())
  }
}

#[tauri::command]
async fn checkout_branch(name: String, state: State<'_, Mutex<Option<Repository>>>) -> Result<Branch, String> {
  match &*state.inner().lock().expect("Could not lock mutex") {
    Some(repo) => Branch::by_name(repo, &name)?.checkout(repo),
    None => Err("No open repo".to_string())
  }
}

#[tauri::command]
async fn statuses(state: State<'_, Mutex<Option<Repository>>>) -> Result<Vec<Status>, String> {
  match &*state.inner().lock().expect("Could not lock mutex") {
    Some(repo) => Ok(Status::all(repo)),
    None => Err("No open repo".to_string())
  }
}

#[tauri::command]
async fn commit_diff(id: String, state: State<'_, Mutex<Option<Repository>>>) -> Result<Vec<Status>, String> {
  match &*state.inner().lock().expect("Could not lock mutex") {
    Some(repo) => Ok(Commit::diff_files(repo, id)),
    None => Err("No open repo".to_string())
  }
}

#[tauri::command]
async fn commits(branches: Vec<String>, state: State<'_, Mutex<Option<Repository>>>) -> Result<Vec<Commit>, String> {
  match &*state.inner().lock().expect("Could not lock mutex") {
    Some(repo) => Ok(Commit::listed(repo, branches)),
    None => Err("No open repo".to_string())
  }
}

#[tauri::command]
async fn head(state: State<'_, Mutex<Option<Repository>>>) -> Result<Commit, String> {
  match &*state.inner().lock().expect("Could not lock mutex") {
    Some(repo) => {
      let head = repo.head().unwrap().peel_to_commit().unwrap();
      Ok(Commit::from_git2(head))
    },
    None => Err("No open repo".to_string())
  }
}

#[tauri::command]
async fn open_repo(path: String, state: State<'_, Mutex<Option<Repository>>>) -> Result<String, String> {
  match Repository::open(&path) {
    Ok(repo) => {
      let mut state = state.lock().expect("Could not lock mutex");
      *state = Some(repo);
      Ok(path)
    },
    Err(e) => {
      println!("{}", e);
      Err(e.message().into())
    }
  }
}

fn main() {
  let repo: Mutex<Option<Repository>> = Mutex::new(None);
  tauri::Builder::default()
    .manage(repo)
    .invoke_handler(tauri::generate_handler![
      open_repo,
      branch_locals,
      statuses,
      commits,
      head,
      commit_diff,
      checkout_branch,
    ])
    .run(tauri::generate_context!())
    .expect("error while running tauri application");
}
