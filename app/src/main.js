import Vue from "vue"
import App from "./App.vue"
import router from "./router"
import store from "./store"
import VueResource from "vue-resource"
import VueYoutube from "vue-youtube"

Vue.config.productionTip = false

Vue.use(VueResource)
Vue.use(VueYoutube)


new Vue({
  router,
  store,
  render: h => h(App)
}).$mount("#app")
