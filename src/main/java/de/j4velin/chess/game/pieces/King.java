/*
 * Copyright 2014 Thomas Hoffmann
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.j4velin.chess.game.pieces;

import java.util.LinkedList;
import java.util.List;

import de.j4velin.chess.game.Coordinate;

public class King extends Piece {

    public King(final Coordinate p, final String o) {
        super(p, o);
    }

    @Override
    public List<Coordinate> getPossiblePositions() {
        List<Coordinate> re = new LinkedList<Coordinate>();
        int x = position.x;
        int y = position.y;
        Coordinate c;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                c = new Coordinate(x + i, y + j);
                if (c.isValid() && !sameTeam(c)) {
                    re.add(c);
                }
            }
        }
        return re;
    }

    @Override
    public String getString() {
        return "\u265A";
    }
}
