<template>
    <div>
        <youtube :video-id="videoId" @ready="ready" @ended="ended" @playing="playing" @paused="paused"
                 :player-vars="{ autoplay: 1 }"></youtube>
        <button @click.prevent="previous">Previous</button>
        <button @click.prevent="next">Next</button>
        <br>video id: {{videoId}}
        <br/> queue: {{ids}}
    </div>
</template>

<script>
    let i = 0;
    export default {
        name: "player",
        data() {
            return {
                videoId: "",
                ids: ["cdwal5Kw3Fc", "t_6sbEEIvjI", "RHg53wMflCc"]
            }
        },
        methods: {
            change() {
                this.videoId = this.ids[Math.abs(Math.abs(i % this.ids.length))];
            },
            previous() {
                i--;
                this.change();
            },
            next() {
                i++;
                this.change();
            },
            ended(event) {
                console.log(event);
                this.next();
            },
            playing(event) {
                console.log(event);
            },
            paused(event) {
                console.log(event);
            },
            ready(event) {
                this.change()
            }
        }
    }
</script>
