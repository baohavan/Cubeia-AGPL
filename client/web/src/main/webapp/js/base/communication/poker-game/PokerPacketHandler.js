"use strict";
var Poker = Poker || {};


Poker.PokerPacketHandler = Class.extend({

    /**
     * @type Poker.TableManager
     */
    tableManager : null,
    tableId : null,
    init : function(tableId) {
        this.tableId = tableId;
        this.tableManager = Poker.AppCtx.getTableManager();
    },
    handleRequestAction : function(requestAction) {

        this.tableManager.updateMainPot(this.tableId,requestAction.currentPotSize);

        Poker.PokerSequence.setSequence(this.tableId,requestAction.seq);

        var acts = Poker.ActionUtils.getPokerActions(requestAction.allowedActions);

        if(acts.length>0 && (acts[0].type.id == Poker.ActionType.BIG_BLIND.id || acts[0].type.id == Poker.ActionType.SMALL_BLIND.id)) {
            //for now auto post blinds
            console.log("Auto posting " + acts[0].type.text);
            var action = Poker.ActionUtils.getPlayerAction(this.tableId,requestAction.seq, requestAction.allowedActions[0].type,
                requestAction.allowedActions[0].minAmount);
            new Poker.PokerRequestHandler(this.tableId).sendGameTransportPacket(action);



            return;
        }
        this.tableManager.handleRequestPlayerAction(
            this.tableId,
            requestAction.player,
            acts,
            requestAction.timeToAct);

    },
    handlePlayerBalance : function(packet) {
        this.tableManager.updatePlayerBalance(this.tableId,
            packet.player,
            Poker.Utils.formatCurrency(packet.balance)
        );
    },
    handlePlayerHandStartStatus : function(packet) {
        var status = Poker.PlayerTableStatus.SITTING_OUT;
        if(packet.status == com.cubeia.games.poker.io.protocol.PlayerTableStatusEnum.SITIN){
            status = Poker.PlayerTableStatus.SITTING_IN;
        }
        this.tableManager.updatePlayerStatus(this.tableId,packet.player, status);
    },
    handleBuyIn : function(protocolObject) {
        var po = protocolObject;
        console.log("BUY-IN:");
        console.log(protocolObject);
        this.tableManager.handleBuyInInfo(this.tableId,po.balanceInWallet, po.balanceOnTable, po.maxAmount, po.minAmount,po.mandatoryBuyin);
    },
    handlePerformAction : function(performAction){
        var actionType = Poker.ActionUtils.getActionType(performAction.action.type);

        var amount = 0;
        if(performAction.betAmount) {
            amount = Poker.Utils.formatCurrency(performAction.betAmount);
        }
        this.tableManager.handlePlayerAction(this.tableId,performAction.player,actionType,amount);
    },
    handleDealPublicCards : function(packet) {
        this.tableManager.bettingRoundComplete(this.tableId);
        for ( var i = 0; i < packet.cards.length; i ++ ) {
            this.tableManager.dealCommunityCard(this.tableId,packet.cards[i].cardId,
                Poker.Utils.getCardString(packet.cards[i]));
        }
    },
    handleDealPrivateCards : function(protocolObject) {
        var cardsToDeal = protocolObject.cards;
        for(var c in cardsToDeal) {
            var cardString = Poker.Utils.getCardString(cardsToDeal[c].card);
            this.tableManager.dealPlayerCard(this.tableId,cardsToDeal[c].player,cardsToDeal[c].card.cardId,cardString);
        }
    },
    handleExposePrivateCards : function(packet) {
        this.tableManager.bettingRoundComplete(this.tableId);
        for (var i = 0; i < packet.cards.length; i ++ ) {
            this.tableManager.exposePrivateCard(this.tableId,packet.cards[i].card.cardId,
                Poker.Utils.getCardString(packet.cards[i].card));
        }
    },
    handlePlayerPokerStatus : function(packet) {
        var status = packet.status;
        switch (status) {
            case com.cubeia.games.poker.io.protocol.PlayerTableStatusEnum.SITIN :
                this.tableManager.updatePlayerStatus(this.tableId,packet.player, Poker.PlayerTableStatus.SITTING_IN);
                break;
            case com.cubeia.games.poker.io.protocol.PlayerTableStatusEnum.SITOUT :
                this.tableManager.updatePlayerStatus(this.tableId,packet.player, Poker.PlayerTableStatus.SITTING_OUT);
                break;
        }
    },
    handlePotTransfers : function(packet) {
        var pots = [];
        for(var i in packet.pots) {
            var p = packet.pots[i];
            var type = Poker.PotType.MAIN;
            if(com.cubeia.games.poker.io.protocol.PotTypeEnum.SIDE == p.type) {
                type = Poker.PotType.SIDE;
            }
            pots.push(new Poker.Pot(p.id,type, p.amount));
        }
        if(pots.length>0) {
            this.tableManager.updatePots(this.tableId,pots);
        }
    }
});