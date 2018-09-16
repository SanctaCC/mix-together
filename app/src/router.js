import Vue from "vue"
import Router from "vue-router"
import Home from "./views/Home.vue"
import Player from "./views/Player.vue"
import Remote from "./views/Remote.vue"

Vue.use(Router)

export default new Router({
  mode: "history",
  base: process.env.BASE_URL,
  routes: [
    {
      path: "/",
      name: "home",
      component: Home
    },
    {
      path: "/about",
      name: "about",
      // route level code-splitting
      // this generates a separate chunk (about.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () =>
        import(/* webpackChunkName: "about" */ "./views/About.vue")
    },
    {
      path: "/code/:code/slave",
        params:true,
      name: "player",
      component: Player
    },
      {
        path: "/code/:code/master",
          name: "remote",
          component: Remote
      }
  ]
})
