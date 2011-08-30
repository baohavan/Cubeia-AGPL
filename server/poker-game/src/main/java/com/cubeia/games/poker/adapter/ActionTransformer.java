/**
 * Copyright (C) 2010 Cubeia Ltd <info@cubeia.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cubeia.games.poker.adapter;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import se.jadestone.dicearena.game.telesina.network.protocol.BestHand;
import se.jadestone.dicearena.game.telesina.network.protocol.CardToDeal;
import se.jadestone.dicearena.game.telesina.network.protocol.DealPrivateCards;
import se.jadestone.dicearena.game.telesina.network.protocol.DealPublicCards;
import se.jadestone.dicearena.game.telesina.network.protocol.Enums;
import se.jadestone.dicearena.game.telesina.network.protocol.Enums.ActionType;
import se.jadestone.dicearena.game.telesina.network.protocol.Enums.PotType;
import se.jadestone.dicearena.game.telesina.network.protocol.ExposePrivateCards;
import se.jadestone.dicearena.game.telesina.network.protocol.GameCard;
import se.jadestone.dicearena.game.telesina.network.protocol.HandEnd;
import se.jadestone.dicearena.game.telesina.network.protocol.PerformAction;
import se.jadestone.dicearena.game.telesina.network.protocol.PlayerAction;
import se.jadestone.dicearena.game.telesina.network.protocol.PlayerBalance;
import se.jadestone.dicearena.game.telesina.network.protocol.Pot;
import se.jadestone.dicearena.game.telesina.network.protocol.RequestAction;
import ca.ualberta.cs.poker.Card;
import ca.ualberta.cs.poker.Hand;
import ca.ualberta.cs.poker.HandEvaluator;

import com.cubeia.firebase.api.action.GameDataAction;
import com.cubeia.games.poker.util.ProtocolFactory;
import com.cubeia.poker.action.ActionRequest;
import com.cubeia.poker.action.PokerAction;
import com.cubeia.poker.action.PokerActionType;
import com.cubeia.poker.action.PossibleAction;
import com.cubeia.poker.model.PlayerHands;
import com.cubeia.poker.player.PokerPlayer;

/**
 * Translates poker-logic internal actions to the styx wire-protocol
 * as defined in poker-protocol.
 *
 * @author Fredrik Johansson, Cubeia Ltd
 */
public class ActionTransformer {
    
    @SuppressWarnings("unused")
    private static transient Logger log = Logger.getLogger(ActionTransformer.class);
    
    private static AtomicInteger sequenceCounter = new AtomicInteger();
	
	public static RequestAction transform(ActionRequest request) {
		RequestAction packet = new RequestAction();
		packet.timeToAct = (int)request.getTimeToAct();
		packet.player = request.getPlayerId();
		packet.seq = getNextSequence();
		
		List<PlayerAction> allowed = new LinkedList<PlayerAction>();
		for (PossibleAction option : request.getOptions()) {
			PlayerAction playerOption = createPlayerAction(option.getActionType());
			// FIXME: Casting to integer here since Flash does not support long values!
			playerOption.minAmount = (int)option.getMinAmount();
			playerOption.maxAmount = (int)option.getMaxAmount();
			allowed.add(playerOption);
		}
		packet.allowedActions = allowed;
		
		return packet;
	}
	
	public static PerformAction transform(PokerAction pokerAction, PokerPlayer pokerPlayer) {
		PerformAction packet = new PerformAction();
		PlayerAction action = createPlayerAction(pokerAction.getActionType());
		packet.action = action;
		// FIXME: Flash does not support longs...
		packet.betAmount = (int) pokerAction.getBetAmount();
		packet.raiseAmount = (int) pokerAction.getRaiseAmount();
		packet.stackAmount = (int) pokerPlayer.getBetStack();
		packet.player = pokerAction.getPlayerId();
		packet.timeout = pokerAction.isTimeout();
		packet.balance = (int)pokerPlayer.getBalance();
		return packet;
	}
	
	public static PokerActionType transform(ActionType protocol) {
		PokerActionType type;
		switch(protocol) {
			case FOLD:
				type = PokerActionType.FOLD;
				break;
				
			case CHECK:
				type = PokerActionType.CHECK;
				break;
				
			case CALL:
				type = PokerActionType.CALL;
				break;
				
			case BET:
				type = PokerActionType.BET;
				break;
				
			case BIG_BLIND:
				type = PokerActionType.BIG_BLIND;
				break;
				
			case SMALL_BLIND:
				type = PokerActionType.SMALL_BLIND;
				break;
				
			case RAISE:
				type = PokerActionType.RAISE;
				break;
				
			case DECLINE_ENTRY_BET:
				type = PokerActionType.DECLINE_ENTRY_BET;
				break;
				
			default:
				type = PokerActionType.FOLD;
				break;
		}
		return type;
	}
	
	
	private static PlayerAction createPlayerAction(PokerActionType actionType) {
		PlayerAction action = new PlayerAction();
		switch(actionType) {
			case FOLD:
				action.type = ActionType.FOLD;
				break;
				
			case CHECK:
				action.type = ActionType.CHECK;
				break;
				
			case CALL:
				action.type = ActionType.CALL;
				break;
				
			case BET:
				action.type = ActionType.BET;
				break;
				
			case BIG_BLIND:
				action.type = ActionType.BIG_BLIND;
				break;
				
			case SMALL_BLIND:
				action.type = ActionType.SMALL_BLIND;
				break;
				
			case RAISE:
				action.type = ActionType.RAISE;
				break;
				
			default:
				action.type = ActionType.FOLD;
				break;
		}
		
		return action;
	}

	/**
	 * 
	 * @param playerId, the player receiving the cards
	 * @param cards, the cards to be dealt
	 * @param hidden, true if the suit and rank should be of type HIDDEN only
	 * @return
	 */
	public static DealPrivateCards createPrivateCardsPacket(int playerId, List<Card> cards, boolean hidden) {
		DealPrivateCards packet = new DealPrivateCards();
		packet.cards = new LinkedList<CardToDeal>();
		for (Card card : cards) {
			GameCard gCard = new GameCard();
			// gCard.cardId = card.getDeckId();
			gCard.cardId = card.getIndex(); // FIXME - wrong ID
			
			if (!hidden) {
				gCard.rank = Enums.Rank.values()[card.getRank()];
				gCard.suit = Enums.Suit.values()[card.getSuit()];
			} else {
				gCard.rank = Enums.Rank.HIDDEN;
				gCard.suit = Enums.Suit.HIDDEN;
			}
			
			CardToDeal deal = new CardToDeal();
			deal.player = playerId;
			deal.card = gCard;
			packet.cards.add(deal);
		}
		return packet;
	}
	
	public static DealPublicCards createPublicCardsPacket(List<Card> cards) {
		DealPublicCards packet = new DealPublicCards();
		packet.cards = new LinkedList<GameCard>();
		for (Card card : cards) {
			GameCard gCard = new GameCard();
			
			gCard.rank = Enums.Rank.values()[card.getRank()];
			gCard.suit = Enums.Suit.values()[card.getSuit()];
			// gCard.cardId = card.getDeckId();
			gCard.cardId = card.getIndex(); // FIXME - wrong ID
			
			packet.cards.add(gCard);
		}
		return packet;
	}
	
	public static ExposePrivateCards createExposeCardsPacket(int playerId, List<Card> cards) {
		ExposePrivateCards packet = new ExposePrivateCards();
		packet.cards = new LinkedList<CardToDeal>();
		for (Card card : cards) {
			GameCard gCard = new GameCard();
			gCard.rank = Enums.Rank.values()[card.getRank()];
			gCard.suit = Enums.Suit.values()[card.getSuit()];
			// gCard.cardId = card.getDeckId();
			gCard.cardId = card.getIndex(); // FIXME - wrong ID
			
			CardToDeal deal = new CardToDeal(playerId, gCard);
			packet.cards.add( deal);
		}
		return packet;
	}

	public static HandEnd createHandEndPacket(PlayerHands hands) {
		HandEnd packet = new HandEnd();
		packet.hands = new LinkedList<BestHand>();
		
		for (Integer pid : hands.getHands().keySet()) {
			BestHand best = new BestHand();
			best.player = pid;
			Hand hand = hands.getHands().get(pid);
			best.rank = HandEvaluator.rankHand(hand);
			best.name = HandEvaluator.nameHand(hand);
			
			packet.hands.add(best);
		}
		
		return packet;
	}
	
	public static Pot createPotUpdatePacket(int id, long amount) {
	    Pot packet = new Pot();
	    packet.amount = (int) amount;
	    packet.id = (byte) id; 
	    packet.type = id == 0 ? PotType.MAIN : PotType.SIDE; 
	    return packet;
	}
	
	
	public static GameDataAction createPlayerBalanceAction(int balance, int playerId, int tableId) {
		return ProtocolFactory.createGameAction(new PlayerBalance(balance, playerId), playerId, tableId);
	}
	
	private static int getNextSequence() {
	    int seq = sequenceCounter.incrementAndGet();
	    if (seq < 0) {
	        // This is not thread safe in the respect that we might set
	        // the counter to 0 multiple times. However, this should be
	        // fine since we will not be able to this on the same table.
	        seq = 0;
	        sequenceCounter.set(seq);
	    }
	    return seq;
	}


}	
