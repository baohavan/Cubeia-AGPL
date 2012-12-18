var Poker = Poker || {};

/**
 * Handles Firebase communications, packages recieved
 * are delegated to the different *PacketHandler classes
 *
 * @type {Poker.CommunicationManager}
 */
Poker.CommunicationManager = Class.extend({

    /**
     * @type FIREBASE.Connector
     */
    connector : null,

    webSocketUrl : null,
    webSocketPort : null,

    /**
     * @type Poker.TableManager
     */
    tableManager : null,

    /**
     *
     * @param {String} webSocketUrl
     * @param {Number} webSocketPort
     * @constructor
     */
    init : function(webSocketUrl, webSocketPort) {
        this.webSocketUrl = webSocketUrl;
        this.webSocketPort = webSocketPort;
        this.tableManager = Poker.AppCtx.getTableManager();
        this.connect();
    },
    /**
     *
     * @return {FIREBASE.Connector}
     */
    getConnector : function() {
        return this.connector;
    },
    /**
     * Callback for lobby packages
     * @param protocolObject
     */
    lobbyCallback : function(protocolObject) {
        console.log("Lobby protocol obj");
        console.log(protocolObject);
        var lobbyPacketHandler = new Poker.LobbyPacketHandler();
        switch (protocolObject.classId) {
            case FB_PROTOCOL.TableSnapshotListPacket.CLASSID :
                lobbyPacketHandler.handleTableSnapshotList(protocolObject.snapshots);
                break;
            case FB_PROTOCOL.TableUpdateListPacket.CLASSID :
                lobbyPacketHandler.handleTableUpdateList(protocolObject.updates);
                break;
            case FB_PROTOCOL.TableRemovedPacket.CLASSID :
                lobbyPacketHandler.handleTableRemoved(protocolObject.tableid);
                break;
            case FB_PROTOCOL.TournamentSnapshotListPacket.CLASSID :
                lobbyPacketHandler.handleTournamentSnapshotList(protocolObject.snapshots);
                break;
            case FB_PROTOCOL.TournamentUpdateListPacket.CLASSID :
                lobbyPacketHandler.handleTournamentUpdates(protocolObject.updates);
                break;
            case FB_PROTOCOL.TournamentRemovedPacket.CLASSID:
                lobbyPacketHandler.handleTournamentRemoved(protocolObject.mttid);
                break;
        }
    },
    /**
     * Login callback
     * @param {FIREBASE.ConnectionStatus} status
     * @param {Number} playerId
     * @param {String} name
     */
    loginCallback : function(status,playerId,name) {
       new Poker.UserPacketHandler().handleLogin(status,playerId,name);
    },
    retryCount : 0,
    /**
     * Callback for connection status
     * @param {FIREBASE.ConnectionStatus} status
     */
    statusCallback : function(status) {
        console.log("Status recevied: " + status);
        var self = this;
        new Poker.UserPacketHandler().handleStatus(function(){self.connect();},status);
    },
    /**
     * Sets up callbacks and connects to Firebase
     */
    connect : function () {
        var self = this;

        this.connector = new FIREBASE.Connector(
            function(po) {
                self.handlePacket(po);
            },
            function(po){
                self.lobbyCallback(po);
            },
            function(status, playerId, name){
                self.loginCallback(status,playerId,name);
            },
            function(status){
                self.statusCallback(status);
            });

        this.connector.connect("FIREBASE.WebSocketAdapter", this.webSocketUrl, this.webSocketPort, "socket");
    },
    /**
     * Calls the connectors login function
     * @param {String} username
     * @param {String} password
     */
    doLogin : function(username,password) {
        this.connector.login(username, password, 0);
    },
    handlePacket : function (packet) {

        var tournamentId = -1;
        if(packet.mttid) {
            tournamentId = packet.mttid;
        }
        var tournamentPacketHandler = new Poker.TournamentPacketHandler(tournamentId);
        var tableId = -1;
        if(packet.tableid) {
            tableId = packet.tableid;
        }
        var tablePacketHandler = new Poker.TablePacketHandler(tableId);
        switch (packet.classId) {
            case FB_PROTOCOL.NotifyJoinPacket.CLASSID :
                tablePacketHandler.handleNotifyJoin(packet);
                break;
            case FB_PROTOCOL.NotifyLeavePacket.CLASSID :
                tablePacketHandler.handleNotifyLeave(packet);
                break;
            case FB_PROTOCOL.SeatInfoPacket.CLASSID :
                tablePacketHandler.handleSeatInfo(packet);
                break;
            case FB_PROTOCOL.JoinResponsePacket.CLASSID :
                tablePacketHandler.handleJoinResponse(packet);
                break;
            case FB_PROTOCOL.GameTransportPacket.CLASSID :
                this.handleGameDataPacket(packet);
                break;
            case FB_PROTOCOL.UnwatchResponsePacket.CLASSID:
                tablePacketHandler.handleUnwatchResponse(packet);
                break;
            case FB_PROTOCOL.LeaveResponsePacket.CLASSID:
                tablePacketHandler.handleLeaveResponse(packet);
                break;
            case FB_PROTOCOL.WatchResponsePacket.CLASSID:
                tablePacketHandler.handleWatchResponse(packet);
                break;
            case FB_PROTOCOL.MttSeatedPacket.CLASSID:
                tournamentPacketHandler.handleSeatedAtTournamentTable(packet);
                break;
            case FB_PROTOCOL.MttRegisterResponsePacket.CLASSID:
                tournamentPacketHandler.handleRegistrationResponse(packet);
                break;
            case FB_PROTOCOL.MttUnregisterResponsePacket.CLASSID:
                tournamentPacketHandler.handleUnregistrationResponse(packet);
                break;
            case FB_PROTOCOL.MttTransportPacket.CLASSID:
                tournamentPacketHandler.handleTournamentTransport(packet);
                break;
            case FB_PROTOCOL.MttPickedUpPacket.CLASSID:
                tournamentPacketHandler.handleRemovedFromTournamentTable(packet);
                break;
            case FB_PROTOCOL.LocalServiceTransportPacket.CLASSID:
                this.handleLocalServiceTransport(packet);
                break;
            case FB_PROTOCOL.NotifyRegisteredPacket.CLASSID:
                tournamentPacketHandler.handleNotifyRegistered(packet);
                break;
            default :
                console.log("NO HANDLER");
                console.log(packet);
                break;
        }
    },
    handleLocalServiceTransport : function(packet) {
        var byteArray = FIREBASE.ByteArray.fromBase64String(packet.servicedata);
        var message = utf8.fromByteArray(byteArray);
        var config = JSON.parse(message);
        Poker.OperatorConfig.populate(config);
        console.log(config);

    },

    handleGameDataPacket:function (gameTransportPacket) {
        if(Poker.Settings.isEnabled(Poker.Settings.Param.FREEZE_COMMUNICATION,null)==true) {
            return;
        }
        if(!this.tableManager.tableExist(gameTransportPacket.tableid)) {
            console.log("Received packet for table ("+gameTransportPacket.tableid+") you're not viewing");
            return;
        }
        var tableId = gameTransportPacket.tableid;
        var valueArray =  FIREBASE.ByteArray.fromBase64String(gameTransportPacket.gamedata);
        var gameData = new FIREBASE.ByteArray(valueArray);
        var length = gameData.readInt();
        var classId = gameData.readUnsignedByte();

        var protocolObject = com.cubeia.games.poker.io.protocol.ProtocolObjectFactory.create(classId, gameData);

        var pokerPacketHandler = new Poker.PokerPacketHandler(tableId);

        switch (protocolObject.classId() ) {
            case com.cubeia.games.poker.io.protocol.GameState.CLASSID:
                this.tableManager.notifyBlindsUpdated(tableId, protocolObject.currentLevel, protocolObject.secondsToNextLevel);
                break;
            case com.cubeia.games.poker.io.protocol.BestHand.CLASSID:
                this.tableManager.updateHandStrength(tableId,protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.BuyInInfoRequest.CLASSID:
                console.log("UNHANDLED PO BuyInInfoRequest");
                console.log(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.BuyInInfoResponse.CLASSID:
                pokerPacketHandler.handleBuyIn(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.BuyInResponse.CLASSID:
                console.log("BUY-IN RESPONSE ");
                console.log(protocolObject);
                this.tableManager.handleBuyInResponse(tableId,protocolObject.resultCode);
                break;
            case com.cubeia.games.poker.io.protocol.CardToDeal.CLASSID:
                console.log("UNHANDLED PO CardToDeal");
                console.log(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.DealerButton.CLASSID:
                this.tableManager.setDealerButton(tableId,protocolObject.seat);
                break;
            case com.cubeia.games.poker.io.protocol.DealPrivateCards.CLASSID:
                pokerPacketHandler.handleDealPrivateCards(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.DealPublicCards.CLASSID:
                pokerPacketHandler.handleDealPublicCards(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.DeckInfo.CLASSID:
                console.log("UNHANDLED PO DeckInfo");
                console.log(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.ErrorPacket.CLASSID:
                console.log("UNHANDLED PO ErrorPacket");
                console.log(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.ExposePrivateCards.CLASSID:
                pokerPacketHandler.handleExposePrivateCards(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.ExternalSessionInfoPacket.CLASSID:
                console.log("UNHANDLED PO ExternalSessionInfoPacket");
                console.log(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.FuturePlayerAction.CLASSID:
                console.log("UNHANDLED PO FuturePlayerAction");
                console.log(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.GameCard.CLASSID:
                console.log("UNHANDLED PO GameCard");
                console.log(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.HandCanceled.CLASSID:
                console.log("UNHANDLED PO HandCanceled");
                console.log(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.HandEnd.CLASSID:
                this.tableManager.endHand(tableId,protocolObject.hands,protocolObject.potTransfers);
                break;
            case com.cubeia.games.poker.io.protocol.InformFutureAllowedActions.CLASSID:
                console.log("UNHANDLED PO InformFutureAllowedActions");
                console.log(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.PerformAction.CLASSID:
                pokerPacketHandler.handlePerformAction(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.PingPacket.CLASSID:
                console.log("UNHANDLED PO PingPacket");
                console.log(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.PlayerAction.CLASSID:
                console.log("UNHANDLED PO PlayerAction");
                console.log(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.PlayerBalance.CLASSID:
                pokerPacketHandler.handlePlayerBalance(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.PlayerDisconnectedPacket.CLASSID:
                console.log("UNHANDLED PO PlayerDisconnectedPacket");
                console.log(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.PlayerHandStartStatus.CLASSID:
                pokerPacketHandler.handlePlayerHandStartStatus(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.PlayerPokerStatus.CLASSID:
                pokerPacketHandler.handlePlayerPokerStatus(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.PlayerReconnectedPacket.CLASSID:
                console.log("UNHANDLED PO PlayerReconnectedPacket");
                console.log(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.PlayerState.CLASSID:
                console.log("UNHANDLED PO PlayerState");
                console.log(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.PongPacket.CLASSID:
                console.log("UNHANDLED PO PongPacket");
                console.log(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.PotTransfers.CLASSID:
                pokerPacketHandler.handlePotTransfers(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.RakeInfo.CLASSID:
                console.log("UNHANDLED PO RakeInfo");
                console.log(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.RequestAction.CLASSID:
                pokerPacketHandler.handleRequestAction(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.StartHandHistory.CLASSID:
                console.log("UNHANDLED PO StartHandHistory");
                console.log(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.HandStartInfo.CLASSID:
                this.tableManager.startNewHand(tableId, protocolObject.handId);
                break;
            case com.cubeia.games.poker.io.protocol.StopHandHistory.CLASSID:
                console.log("UNHANDLED PO StopHandHistory");
                console.log(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.TakeBackUncalledBet.CLASSID:
                console.log("UNHANDLED PO TakeBackUncalledBet");
                console.log(protocolObject);
                break;
            case com.cubeia.games.poker.io.protocol.WaitingToStartBreak:
                this.tableManager.notifyWaitingToStartBreak();
                break;
            case com.cubeia.games.poker.io.protocol.BlindsAreUpdated.CLASSID:
                this.tableManager.notifyBlindsUpdated(tableId, protocolObject.level, protocolObject.secondsToNextLevel);
                break;
            case com.cubeia.games.poker.io.protocol.TournamentDestroyed.CLASSID:
                this.tableManager.notifyTournamentDestroyed(tableId);
                break;
            default:
                console.log("Ignoring packet");
                console.log(protocolObject);
                break;
        }
    }
});

