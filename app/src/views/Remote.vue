<template>
    <div class="main">
        <div>
            <input type="range" min="0" max="100" v-model="volumeSlider"
                   @change="sendCommand('volume-'+volumeSlider)">
            <div>
                <button class="ctrl-btn" id="pause" @click='sendCommand("pause")'> PAUSE
                </button>
                <button class="ctrl-btn" id="play" v-on:click='sendCommand("play")'> PLAY
                </button>
            </div>
            <div>
                <button class="ctrl-btn" @click.prevent="previous">Previous</button>
                <button class="ctrl-btn" @click.prevent="next">Next</button>
            </div>
        </div>
        <p> Slaves available : {{subscriptions}} {{(subscriptions>0)? '&#9989;':'&#x2757;'}} </p>
        video id: {{videoId}}
        <br/> <h5 style="overflow: hidden">{{code}} </h5>
        <div class="playlist-wrapper">
            <div class="btn-div" v-for="value in ids">
                <button class="movie-btn" v-on:click="switchTo(value)" v-bind:class="{red: videoId == value.id}">
                    {{ (value.title != undefined)? value.title : value.id}}
                </button>
                <button class="remove-btn" v-on:click="remove(ids.indexOf(value))">X</button>
            </div>
            <form @submit.prevent="addMovie()">
                <input class="add-movie" v-model="newVideoInput" placeholder="youtube video or playlist url">
                <button> add new</button>
            </form>
            <button @click.prevent="shuffle">Shuffle</button>
        </div>
    </div>
</template>

<script>
    import Vue from "vue"

    var i = 0;
    export default {
        name: "remote",
        created() {
            this.initSSE();
            var codestring = this.code;
            Vue.http.get("http://localhost:8080/api/codes/" + codestring + "/movies").then(response => {
                var responses = (response.body._embedded.movieResourceList);
                this.videoId = responses[0].url;
                responses.forEach(p => {
                    this.ids.push({id: p.url, order: p.order, title: p.title});
                })
            });

            window.addEventListener('scroll', this.handleScroll);
        },
        data() {
            return {
                code: this.$route.params.code,
                videoId: "",
                ids: [],
                newVideoInput: "",
                volumeSlider: 100,
                subscriptions: 0
            }
        },
        methods: {
            handleScroll () {
                var docHeight = document.documentElement.getBoundingClientRect().height
                var winHeight = window.innerHeight;
                var scrollPercent = (window.scrollY) / (docHeight - winHeight);
                this.sendCommand('scroll-'+scrollPercent);
            },
            initSSE() {
                Vue.http.get("http://localhost:8080/api/codes/" + this.code + "/status")
                    .then(value => this.subscriptions = value.data.subscriptions);
                let es = new EventSource('http://localhost:8080/api/codes/' + this.code + '/flux');

                es.addEventListener('message', event => {
                    let data = JSON.parse(event.data);
                    if (data.STATUS == 'CONNECTED') {
                        this.subscriptions++;
                        this.sendCommand("volume-" + this.volumeSlider);
                        this.sendCommand("switch-" + i);
                    }
                    else if (data.STATUS == 'DISCONNECTED') {
                        this.subscriptions--;
                    }
                }, true);
            },
            sendCommand: function (command) {
                return Vue.http.post("http://localhost:8080/api/codes/" + this.code + "/movies?command=" + command);
            },
            change() {
                this.videoId = this.ids[(i % this.ids.length)].id;
            },
            previous() {
                this.sendCommand("previous");
                i = (i - 1 < 0) ? this.ids.length - 1 : i - 1;
                this.change();
            },
            next() {
                this.sendCommand("next");
                i++;
                this.change();
            },
            switchTo(value) {
                this.sendCommand('switch-' + value.order);
                i = value.order;
                this.change();
            },
            remove(value) {
                Vue.http.delete(
                    "http://localhost:8080/api/codes/" + this.code + "/movies/" + value);
                this.sendCommand("delete");
                if (value === i % (this.ids.length)) {
                    i--;
                    this.ids.splice(value, 1);
                    this.reorder();
                    this.switchTo({order: value});
                    // this.next();
                    // this.change();
                    return;
                } else if (i > value) {
                    i--;
                }
                this.ids.splice(value, 1);
                this.reorder();
            },
            addMovie() {
                var id = this.$youtube.getIdFromUrl(this.newVideoInput);
                var reg = /[&?]list=([^&]+)/i
                var playlistSplit = (reg.exec(this.newVideoInput));
                if (id)
                    Vue.http.post(
                        "http://localhost:8080/api/codes/" + this.code + "/movies", {url: id}).then(p => {
                        p = p.body;
                        this.ids.push({id: p.url, order: p.order, title: p.title});
                    });
                else if (playlistSplit[1]) {
                    Vue.http.post(
                        "http://localhost:8080/api/codes/" + this.code + "/movies?playlistId=" + playlistSplit[1], {
                            headers: {
                                'Content-Type': 'application/json'
                            }
                        }).then(k => {
                        k = k.body;
                        k.forEach(p => {
                            this.ids.push({id: p.url, order: p.order, title: p.title});
                        })
                    });
                }
                this.newVideoInput = '';
            }, reorder() {
                var j = 0;
                this.ids.forEach(p => {
                    p.order = j++;
                });
            },
            shuffle() {
                Vue.http.put(
                    "http://localhost:8080/api/codes/" + this.code + "/movies?shuffle=abc", {
                        headers: {
                            'Content-Type': 'application/json'
                        }
                    }).then(p => {
                    this.ids = [];
                    Vue.http.get("http://localhost:8080/api/codes/" + this.code + "/movies").then(response => {
                        var responses = (response.body._embedded.movieResourceList);
                        responses.forEach(p => {
                            if (this.videoId === p.url) {
                                i = p.order
                            }
                            this.ids.push({id: p.url, order: p.order, title: p.title});
                        });
                        this.sendCommand('switch-' + i);
                    });

                })

            }
        }
    }
</script>

<style scoped>

</style>