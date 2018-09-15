<template>
    <div class="home">
        Playlist name:
        <form @submit.prevent="createPlaylist">
            <input v-bind:autofocus="this" type="text" name="code" id="code" v-model="code">
            <br>
            <p v-on:click="slave=false"><input type="radio" id="master" value="master" v-model="slave"
                                               v-bind:value="false">
                Master (mobile)</p>
            <p v-on:click="slave=true"><input type="radio" id="slave" value="slave" v-model="slave" v-bind:value="true">
                Slave (PC)</p>
        </form>
    </div>
</template>

<script>
    export default {
        name: "home",
        data() {
            return {
                code: "",
                slave: false,
            }
        },
        methods: {
            createPlaylist() {
                this.$store.dispatch("home/createPlaylist", this.code).then(data => {
                    console.log(data);
                }, error => {
                });
                this.$router.push({
                    name: 'player', params: {code: this.code}, query: {slave:  this.slave.toString()}
                })
            }
        }
    }
</script>
