import Vue from "vue";

const api = {
  createPlaylist(code) {
    const body = {
      "code": code
    }
    return Vue.http.post("http://localhost:8080/api/codes", body)
  }
}

export default api