"use strict";
var Poker = Poker || {};

/**
 * The SoundRepository is responsible for loading and caching the sounds in the client.
 *
 * @type {*}
 */
Poker.SoundRepository = Class.extend({
    sounds: null,

    init:function () {
        this.sounds = [];
        this.loadSounds();
    },

    loadSounds:function () {
        var codec = this.getCodec();
        var path = contextPath+"/sounds/" + codec + "/";

        var audioModel = "Audio";
        var context = null;

        if(typeof(Audio)=="undefined") {
            return;
        }

        if(typeof(webkitAudioContext)!="undefined") {
            audioModel = "webkitAudioContext";
            context = new webkitAudioContext();
        }

        for (var sound in Poker.Sounds) {
            var soundList = Poker.Sounds[sound].soundList;
            var soundSources = [];
            for (var i = 0; i < soundList.length; i++) {
                var file = path+Poker.Sounds[sound].soundList[i].file+"."+codec;
                var audio = new Poker.SoundSource(file, audioModel, context);
                audio.setGain(Poker.Sounds[sound].soundList[i].gain);
                console.log("Loading to " + audioModel + " from file " + file);
                soundSources[i] = audio;
            }
            this.sounds[Poker.Sounds[sound].id] = soundSources;
        }
        console.log(this.sounds)
    },

    getSound:function (soundId, selection) {
        console.log(this.sounds[soundId][selection], soundId, this.sounds);
        return this.sounds[soundId][selection];
    },

    getCodec:function() {
        var checkAudio = new Audio();
        if (checkAudio.canPlayType('audio/ogg; codecs="vorbis"')) {
            return "ogg";
        }
        if (checkAudio.canPlayType('audio/mpeg;')) {
            return "mp3";
        }
        if (checkAudio.canPlayType('audio/wav; codecs="1"')) {
            return "wav";
        }
        console.log("no supported audio codec found");
        return "no_sounds";
    }

});