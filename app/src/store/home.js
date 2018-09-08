import api from "./api"

const namespaced = true

const actions = {
  createPlaylist({dispatch}, code) {
    api.createPlaylist(code).then(
      (response) => {
        console.log(response)
      }
    )
  }
}

export default {
  namespaced,
  actions
}