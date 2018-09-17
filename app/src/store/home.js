import api from "./api"

const namespaced = true

const actions = {
  createPlaylist({dispatch}, code) {
    return api.createPlaylist(code)
  }
}

export default {
  namespaced,
  actions
}