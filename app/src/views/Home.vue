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
            continue(result) {

                if (this.slave)
                    this.$router.push({
                        name: 'player', params: {code: result}
                    })
                else
                    this.$router.push({
                        name: 'remote', params: {code: result}
                    })
            },
            createPlaylist() {
                this.$store.dispatch("home/createPlaylist", this.code).then(response => {
                    if (!response)
                        this.continue(this.code);
                    else
                        this.continue(response.body.code);

                        }, error => {
                    this.continue(this.code);
                });

            }
        }
    }
</script>
