<template>
    <div>
        <youtube :video-id="videoId" @ready="ready" @ended="ended" @playing="playing" @paused="paused"
                 :player-vars="{ autoplay: 1 }"></youtube>
        <button @click.prevent="previous">Previous</button>
        <button @click.prevent="next">Next</button>
        <br>video id: {{videoId}}
        <br/> playlist ({{code}}) :
        <div v-for="value in ids">
            <li>
                <button v-on:click="switchTo(value)" v-bind:class="{red: videoId === value.id}">
                    {{ (value.title != undefined)? value.title : value.id}}
                </button>
                <button v-on:click="remove(ids.indexOf(value))">X</button>
            </li>
        </div>
        <form @submit.prevent="addMovie()">
        <input class="add-movie" v-model="newVideo"> <button> add new</button>
        </form>
    </div>
</template>

<script>
    import Vue from "vue"

    let i = 0;
    export default {

        name: "player",

        created() {
            Vue.http.get("http://localhost:8080/api/codes/" + this.code + "/movies").then(response => {
                var responses = (response.body._embedded.movieResourceList);
                responses.forEach(p => {
                    this.ids.push({id: p.url, order: p.order, title: p.title});
                })
            });

        },

        data() {
            return {
                code: this.$route.params.code,
                videoId: "",
                ids: [],
                newVideo: "youtube video or playlist"
            }
        },
        methods: {
            change() {
                this.videoId = this.ids[(i % this.ids.length)].id;
            },
            previous() {
                i = (i-1 <= 0)? this.ids.length : i-1;
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
                i = value.order;
                this.change();
            },
            remove(value) {
                console.log(value);
                if (value === i % (this.ids.length)) {
                    i--;
                    this.ids.splice(value, 1);
                    this.next();
                    return;
                }
                else if (value <= i % (this.ids.length)) {
                    i--;
                }
                this.ids.splice(value, 1);
            },
            ready(event) {
                this.change();
            },
            addMovie() {
                var id = this.$youtube.getIdFromUrl(this.newVideo);
                Vue.http.post("http://localhost:8080/api/codes/" + this.code + "/movies", {url: id}).then(p=> {
                    p = p.body;
                        this.ids.push({id: p.url, order: p.order, title: p.title});
                    });
                this.newVideo = '';
            }
        }

    }
</script>


<style>
    .red {
        background-color: red;
    }
    .add-movie {
        width: 500px;
    }
</style>