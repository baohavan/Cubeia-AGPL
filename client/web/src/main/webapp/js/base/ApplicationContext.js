"use strict";
var Poker = Poker  || {};
/**
 * Class that manages global instances.
 * Usage:
 *  //needs to be executed before anything else,
 *  //usually when the onload event is triggered
 *  Poker.AppCtx.wire();
 *
 *  var tableManager = Poker.AppCtx.getTableManager();
 * @type {Poker.AppCtx}
 */
Poker.AppCtx = Class.extend({
    init : function() {
    },
    /**
     * Creates all the global instances that is needed for the application
     *
     * @param settings
     */
    wire : function(settings) {

        //this
        var templateManager = new Poker.TemplateManager();
        this.getTemplateManager = function() {
            return templateManager;
        };

        var tableManager = new Poker.TableManager();
        this.getTableManager = function() {
            return tableManager;
        };

        var dialogManager = new Poker.DialogManager();
        this.getDialogManager = function() {
            return dialogManager;
        };

        var viewManager = new Poker.ViewManager("tabItems");
        this.getViewManager = function(){
            return viewManager;
        };

        var mainMenuManager = new Poker.MainMenuManager(this.getViewManager());
        this.getMainMenuManager = function() {
            return mainMenuManager;
        };


        var lobbyLayoutManager = new Poker.LobbyLayoutManager();
        this.getLobbyLayoutManager = function() {
            return lobbyLayoutManager;
        };
        /*
         * The only layout manager we only need (?) one instance of,
         * since you only are able to have one lobby open at once
         */
        var lobbyManager = new Poker.LobbyManager();
        this.getLobbyManager = function() {
            return lobbyManager;
        };

        var soundsRepository = new Poker.SoundRepository();
        this.getSoundRepository = function() {
            return soundsRepository;
        };


        var actionSender = new Poker.ActionSender();
        this.getActionSender = function() {
            return actionSender;
        };

        var comHandler = new Poker.CommunicationManager(settings.webSocketUrl, settings.webSocketPort);
        this.getCommunicationManager = function() {
            return comHandler;
        };

        this.getConnector = function() {
            return comHandler.getConnector();
        };

        var tournamentManager = new Poker.TournamentManager(settings.tournamentLobbyUpdateInterval);
        this.getTournamentManager = function() {
            return tournamentManager;
        };





    }
});
Poker.AppCtx = new Poker.AppCtx();