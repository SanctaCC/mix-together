<template>
    <div class="main">

        <youtube class="player" :video-id="videoId" @ready="ready" @playing="onplaying()"
                 @ended="ended" :player-vars="{autoplay: 1}" v-show="slave == 'true'"></youtube>
        <div>
            <input type="range" min="0" max="100"  v-model="volumeSlider"
                   @change="sendCommand('volume-'+volumeSlider)" v-if="slave =='false'">
            <div v-if="slave =='false'">
                <button class="ctrl-btn" id="pause" @click='sendCommand("pause")'> PAUSE
                </button>
                <button class="ctrl-btn" id="play" v-on:click='sendCommand("play")' > PLAY
                </button>
            </div>
            <div>
                <button class="ctrl-btn" @click.prevent="previous">Previous</button>
                <button class="ctrl-btn" @click.prevent="next">Next</button>
            </div>
        </div>
        <br>video id: {{videoId}}
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
    import SockJS from 'sockjs-client'
    import Stomp from 'stomp-websocket'

    let i = 0;
    var stompClient;
    export default {

        name: "player",

        created() {
            var codestring = this.code;
            Vue.http.get("http://localhost:8080/api/codes/" + codestring + "/movies").then(response => {
                var responses = (response.body._embedded.movieResourceList);
                responses.forEach(p => {
                    this.ids.push({id: p.url, order: p.order, title: p.title});
                })
            });
            if (this.slave == 'true')
                this.initWS();
        },
        data() {
            return {
                code: this.$route.params.code,
                slave: this.$route.query.slave,
                videoId: "",
                ids: [],
                newVideoInput: "",
                volumeSlider : 100
            }
        },
        methods: {
            initWS() {
                var wsUrl = "http://localhost:8080/ws";
                var socket = new SockJS(wsUrl);
                stompClient = Stomp.over(socket);
                let code = this.code;
                var ids = this.ids;
                let outerThis = this;
                stompClient.connect({}, function (frame) {
                    stompClient.subscribe("/topic/code/" + code, function (message) {
                        var response = JSON.parse(message.body);
                        if (!response.command)
                            response.forEach(p => {
                                ids.splice(p.order, 1, {id: p.url, order: p.order, title: p.title});
                            });
                        else {
                            switch (response.command) {
                                case "next":
                                    outerThis.next();
                                    break;
                                case "previous":
                                    outerThis.previous();
                                    break;
                                case "play":
                                    outerThis.play();
                                    break;
                                case "pause":
                                    outerThis.pause();
                                    break;
                                case  /^volume/.test(response.command) && response.command:
                                    outerThis.changeVolume(response.command.split('-')[1]);
                                    break;
                            }
                        }
                    });
                }, this);
            },
            sendCommand: function (command) {
                if (this.slave == 'true') return;
                return Vue.http.post("http://localhost:8080/api/codes/" + this.code + "/movies?command=" + command);
            },
            changeVolume(val) {
                this.player.setVolume(val);
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
            ended(event) {
                this.next();
            },
            pause() {
                this.player.pauseVideo();
            },
            play() {
                this.player.playVideo();
            },
            stop() {
                this.player.stopVideo();
            },
            onplaying() {
                if (this.slave == 'false') {
                    this.stop();
                }
            },
            switchTo(value) {
                i = value.order;
                this.change();
            },
            remove(value) {
                Vue.http.delete("http://localhost:8080/api/codes/" + this.code + "/movies/" + value);
                if (value === i % (this.ids.length)) {
                    i--;
                    this.ids.splice(value, 1);
                    this.reorder();
                    this.next();
                    return;
                } else if (i > value) {
                    i--;
                }
                this.ids.splice(value, 1);
                this.reorder();
            },
            ready(event) {
                this.player = event;
                this.change();
            },
            addMovie() {
                var id = this.$youtube.getIdFromUrl(this.newVideoInput);
                Vue.http.post("http://localhost:8080/api/codes/" + this.code + "/movies", {url: id}).then(p => {
                    p = p.body;
                    if (this.slave == 'false')
                        this.ids.push({id: p.url, order: p.order, title: p.title});
                });
                this.newVideoInput = '';
            },
            reorder() {
                var j = 0;
                this.ids.forEach(p => {
                    p.order = j++;
                });
            },
            shuffle() {
                Vue.http.put("http://localhost:8080/api/codes/" + this.code + "/movies?shuffle=abc", {
                    headers: {
                        'Content-Type': 'application/json'
                    }
                });
                if (this.slave == 'false') {
                    this.ids = [];
                    Vue.http.get("http://localhost:8080/api/codes/" + this.code + "/movies").then(response => {
                        var responses = (response.body._embedded.movieResourceList);
                        responses.forEach(p => {
                            if (this.videoId === p.url) {
                                i = p.order
                            }
                            this.ids.push({id: p.url, order: p.order, title: p.title});
                        })
                    });
                }
            }
        }

    }
</script>


<style>
    .red {
        background-color: red;
    }

    .main {
        width: 100%;
        max-width: 500px;
        display: inline-block;
    }

    .player {
        width: 100%;
    }

    .add-movie {
        width: 100%;
        box-sizing: border-box;
    }

    .movie-btn {
        width: 85%;
    }

    .remove-btn {
        width: 15%;
    }

    .btn-div {
        display: flex;
    }

    .ctrl-btn {
        width: 50%;
        min-height: 60px;
    }
</style>