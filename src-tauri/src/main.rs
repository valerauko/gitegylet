#![cfg_attr(
  all(not(debug_assertions), target_os = "windows"),
  windows_subsystem = "windows"
)]

use git2::{Repository, BranchType};

#[tauri::command]
fn branch_locals() -> Vec<String> {
  let repo = Repository::open("/home/valerauko/Projects/climbing").unwrap();
  repo.branches(Some(BranchType::Local)).unwrap().fold(vec![], |mut aggr, branch| match branch {
    Ok((branch, _)) => {
      aggr.push(match branch.name().unwrap() {
        Some(name) => name.to_string(),
        None => "<no name>".into(),
      });
      aggr
    }
    Err(e) => {
      println!("{}", e);
      aggr
    }
  })
}

fn main() {
  tauri::Builder::default()
    .invoke_handler(tauri::generate_handler![branch_locals])
    .run(tauri::generate_context!())
    .expect("error while running tauri application");
}
