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

package com.cubeia.poker.hand;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.ImmutableSet.copyOf;
import static com.google.common.collect.Sets.cartesianProduct;

/**
 * Standard 52-card deck.
 * <p/>
 * This implementation is not thread safe.
 *
 * @author w
 */
public class StandardDeck implements Deck {
    private static final long serialVersionUID = -3518540450503808264L;

    private List<Card> cards;
    private int currentCardIndex = 0;

    public StandardDeck(Shuffler<Card> shuffler, CardIdGenerator idGenerator) {
        List<Card> vanillaDeck = createDeck();
        List<Card> shuffledDeck = shuffler.shuffle(vanillaDeck);
        this.cards = idGenerator.copyAndAssignIds(shuffledDeck);
    }

    @SuppressWarnings("unchecked")
    protected List<Card> createDeck() {
        ArrayList<Card> cards = new ArrayList<Card>();

        for (List<Enum<? extends Enum<?>>> cardContainer : cartesianProduct(copyOf(Suit.values()), copyOf(Rank.values()))) {
            Suit suit = (Suit) cardContainer.get(0);
            Rank rank = (Rank) cardContainer.get(1);
            cards.add(new Card(rank, suit));
        }

        return cards;
    }

    @Override
    public Card deal() {
        if (isEmpty()) {
            throw new IllegalStateException("no more cards in deck");
        }

        return cards.get(currentCardIndex++);
    }

    @Override
    public boolean isEmpty() {
        return currentCardIndex >= cards.size();
    }

    @Override
    public List<Card> getAllCards() {
        return new ArrayList<Card>(cards);
    }
}