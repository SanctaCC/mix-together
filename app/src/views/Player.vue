<template>
  <div>
    <youtube :video-id="videoId" :player-vars="{ autoplay: 1 }"></youtube>
    <button @click.prevent="change">Change</button>
  </div>
</template>

<script>
export default {
  name: "player",
  data() {
    return {
      videoId: "t_6sbEEIvjI"
    }
  },
  methods: {
    change() {
      this.videoId = "i-daiA1RNp4"
    }
  }
}
</script>
