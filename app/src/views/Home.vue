<template>
  <div class="home">
    Playlist name: 
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
      this.$store.dispatch("home/createPlaylist", this.code).then(data => {
        console.log(data);
            }, error => {
            });
      this.$router.push({path: '/player/'+this.code})
    }
  }
}
</script>
