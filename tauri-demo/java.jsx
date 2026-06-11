import { invoke } from '@tauri-apps/api/tauri'

invoke('greet', { name: 'Pranav' })
  .then((response) => console.log(response))