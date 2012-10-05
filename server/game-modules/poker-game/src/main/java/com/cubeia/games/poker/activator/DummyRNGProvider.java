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

package com.cubeia.games.poker.activator;

import java.util.Random;

import com.cubeia.poker.rng.RNGProvider;
import com.google.inject.Singleton;

@Singleton
public class DummyRNGProvider implements RNGProvider {
	
	private static final long serialVersionUID = -7885789555680247568L;
	
	public Random rng = new Random();
	
	@Override
	public Random getRNG() {
		return rng;
	}
}