<template>
    <div class="main">

        <youtube class="player" :video-id="videoId" @ready="ready"
                 @ended="ended" :player-vars="{autoplay: 1}"></youtube>
        <div>
            <div>
                <button class="ctrl-btn" @click.prevent="previous">Previous</button>
                <button class="ctrl-btn" @click.prevent="next">Next</button>
            </div>
        </div>
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
        },
        data() {
            return {
                code: this.$route.params.code,
                videoId: "",
                ids: [],
                newVideoInput: "",
            }
        },
        methods: {
            initWS() {
                var wsUrl = "http://localhost:8080/ws";
                var socket = new SockJS(wsUrl);
                stompClient = Stomp.over(socket);
                let code = this.code;
                let outerThis = this;
                stompClient.connect({code: code}, function (frame) {
                    stompClient.subscribe("/topic/code/" + code, function (message) {
                        var response = JSON.parse(message.body);
                        if (!response.command)
                            response.forEach(p => {
                                outerThis.ids.splice(p.order, 1, {id: p.url, order: p.order, title: p.title});
                            });
                        else {
                            var command = response.command.split('-')[0];
                            switch (command) {
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
                                case "volume":
                                    outerThis.changeVolume(response.command.split('-')[1]);
                                    break;
                                case "switch":
                                    if (outerThis.ids.length == 0) return;
                                    outerThis.switchTo({order:response.command.split('-')[1]});
                                    break;
                                case "delete":
                                    outerThis.remove(outerThis.ids.length-1);
                                    break;
                                case "scroll":
                                    outerThis.scrollTo(response.command.split('-')[1]);
                            }
                        }
                    });
                }, this);
            },
            changeVolume(val) {
                this.player.setVolume(val);
            },
            scrollTo(val) {
                var docHeight = document.documentElement.getBoundingClientRect().height
                var winHeight = window.innerHeight;
                window.scrollTo(0, val * (docHeight - winHeight))
            },
            change() {
                this.videoId = this.ids[(i % this.ids.length)].id;
            },
            previous() {
                i = (i - 1 < 0) ? this.ids.length - 1 : i - 1;
                this.change();
            },
            next() {
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
            switchTo(value) {
                i = value.order;
                this.change();
            },
            remove(value) {
                Vue.http.delete(
                    "http://localhost:8080/api/codes/" + this.code + "/movies/" + value);
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
                if (this.ids.length != 0)
                    this.change();
                this.initWS();
            },
            addMovie() {
                var id = this.$youtube.getIdFromUrl(this.newVideoInput);
                var reg = /[&?]list=([^&]+)/i
                var playlistId = (reg.exec(this.newVideoInput)[1]);
                if (id)
                    Vue.http.post(
                        "http://localhost:8080/api/codes/" + this.code + "/movies", {url: id}).then(p => {});
                else if (playlistId) {
                    Vue.http.post(
                        "http://localhost:8080/api/codes/" + this.code + "/movies?playlistId=" + playlistId, {params: {playlistId}}, {
                            headers: {
                                'Content-Type': 'application/json'
                            }
                        }).then(p => {});
                }
                this.newVideoInput = '';
            },
            reorder() {
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
                    });
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