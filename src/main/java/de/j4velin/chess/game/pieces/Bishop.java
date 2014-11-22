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

import de.j4velin.chess.game.Board;
import de.j4velin.chess.game.Coordinate;

public class Bishop extends Piece {

    public Bishop(final Coordinate p, final String o) {
        super(p, o);
    }

    @Override
    public List<Coordinate> getPossiblePositions() {
        return moveDiagonal(this);
    }

    /**
     * Get a list of possible positions, if the piece can only move diagonal from its current position
     *
     * @param p the piece
     * @return a list of possible positions
     */
    public static List<Coordinate> moveDiagonal(final Piece p) {
        List<Coordinate> re = new LinkedList<Coordinate>();
        int x = p.position.x + 1;
        int y = p.position.y + 1;
        Coordinate c = new Coordinate(x, y);

        // move to top right
        while (c.isValid() && Board.getPiece(c) == null) {
            re.add(c);
            y++;
            x++;
            c = new Coordinate(x, y);
        }
        if (c.isValid() && !p.sameTeam(c)) {
            re.add(c);
        }

        // move to bottom right
        x = p.position.x + 1;
        y = p.position.y - 1;
        c = new Coordinate(x, y);
        while (c.isValid() && Board.getPiece(c) == null) {
            re.add(c);
            y--;
            x++;
            c = new Coordinate(x, y);
        }
        if (c.isValid() && !p.sameTeam(c)) {
            re.add(c);
        }

        // move top left
        x = p.position.x - 1;
        y = p.position.y + 1;
        c = new Coordinate(x, y);
        while (c.isValid() && Board.getPiece(c) == null) {
            re.add(c);
            x--;
            y++;
            c = new Coordinate(x, y);
        }
        if (c.isValid() && !p.sameTeam(c)) {
            re.add(c);
        }

        // move bottom left
        x = p.position.x - 1;
        y = p.position.y - 1;
        c = new Coordinate(x, y);
        while (c.isValid() && Board.getPiece(c) == null) {
            re.add(c);
            x--;
            y--;
            c = new Coordinate(x, y);
        }
        if (c.isValid() && !p.sameTeam(c)) {
            re.add(c);
        }

        return re;
    }

    @Override
    public String getString() {
        return "\u265D";
    }
}
