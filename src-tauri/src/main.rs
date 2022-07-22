#![cfg_attr(
  all(not(debug_assertions), target_os = "windows"),
  windows_subsystem = "windows"
)]

mod branch;
mod status;

use branch::Branch;
use status::Status;
use git2::Repository;
use tauri::State;
use std::sync::Mutex;

#[tauri::command]
async fn branch_locals(state: State<'_, Mutex<Option<Repository>>>) -> Result<Vec<Branch>, String> {
  match &*state.inner().lock().expect("Could not lock mutex") {
    Some(repo) => Ok(Branch::locals(repo)),
    None => Ok(vec![])
  }
}

#[tauri::command]
async fn statuses(state: State<'_, Mutex<Option<Repository>>>) -> Result<Vec<Status>, String> {
  match &*state.inner().lock().expect("Could not lock mutex") {
    Some(repo) => Ok(Status::all(repo)),
    None => Ok(vec![])
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
    ])
    .run(tauri::generate_context!())
    .expect("error while running tauri application");
}
