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

public class Knight extends Piece {

    public Knight(final Coordinate p, final String o) {
        super(p, o);
    }

    @Override
    public List<Coordinate> getPossiblePositions() {
        List<Coordinate> re = new LinkedList<Coordinate>();
        Coordinate c = new Coordinate(position.x + 2, position.y + 1);
        if (c.isValid() && !sameTeam(c)) re.add(c);

        c = new Coordinate(position.x + 2, position.y - 1);
        if (c.isValid() && !sameTeam(c)) re.add(c);

        c = new Coordinate(position.x - 2, position.y + 1);
        if (c.isValid() && !sameTeam(c)) re.add(c);

        c = new Coordinate(position.x - 2, position.y - 1);
        if (c.isValid() && !sameTeam(c)) re.add(c);

        c = new Coordinate(position.x + 1, position.y + 2);
        if (c.isValid() && !sameTeam(c)) re.add(c);

        c = new Coordinate(position.x - 1, position.y + 2);
        if (c.isValid() && !sameTeam(c)) re.add(c);

        c = new Coordinate(position.x + 1, position.y - 2);
        if (c.isValid() && !sameTeam(c)) re.add(c);

        c = new Coordinate(position.x - 1, position.y - 2);
        if (c.isValid() && !sameTeam(c)) re.add(c);

        return re;
    }

    @Override
    public String getString() {
        return "\u265E";
    }
}
