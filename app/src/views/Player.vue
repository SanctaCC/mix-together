<template>
    <div>
        <youtube :video-id="videoId" @ready="ready" @ended="ended" @playing="playing" @paused="paused"
                 :player-vars="{ autoplay: 1 }"></youtube>
        <button @click.prevent="previous">Previous</button>
        <button @click.prevent="next">Next</button>
        <br>video id: {{videoId}}
        <br/> playlist:
        <div v-for="value in ids">
            <li>
                <button v-on:click="switchTo(ids.indexOf(value))" v-bind:class="{red: videoId === value}"> {{value}}
                </button>
                <button v-on:click="remove(ids.indexOf(value))">X</button>
            </li>
        </div>
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
                this.videoId = this.ids[(i % this.ids.length)];
            },
            previous() {
                i = Math.abs(i-1);
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
            switchTo(value) {
                i = value;
                this.change();
            },
            remove(value) {
                if (value === i% (this.ids.length)) {
                    i++;
                    this.ids.splice(value, 1);
                    this.next();
                    this.player.play();
                    return;
                }
                else if (value <= i % (this.ids.length)) {
                    i++;
                }
                this.ids.splice(value, 1);
            },
            ready(event) {
                this.change();
            }
        }
    }
</script>


<style>
    .red {
        background-color: red;
    }
</style>