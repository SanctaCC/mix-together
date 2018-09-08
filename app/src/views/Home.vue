<template>
  <div class="home">
    <form @submit.prevent="createPlaylist">
      <input type="text" name="code" id="code" v-model="code">
    </form>
  </div>
</template>

<script>
export default {
  name: "home",
  data() {
    return {
      code: ""
    }
  },
  methods: {
    createPlaylist() {
      this.$store.dispatch("home/createPlaylist", this.code)
    }
  }
}
</script>
