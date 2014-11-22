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

/**
 * Just like a "normal" pawn, but can only move from right to left
 */
public class LeftPawn extends Piece {
    public LeftPawn(final Coordinate p, final String o) {
        super(p, o);
    }

    @Override
    public List<Coordinate> getPossiblePositions() {
        List<Coordinate> re = new LinkedList<Coordinate>();
        Coordinate c;
        int x = position.x;
        int y = position.y;
        c = new Coordinate(x - 1, y);
        if (c.isValid() && Board.getPiece(c) == null) {
            re.add(c);
        }
        // can move two squares at the beginning
        // (only if no other piece stands 1 before us)
        if (x == 10 && Board.getPiece(c) == null) {
            c = new Coordinate(x - 2, y);
            if (c.isValid() && Board.getPiece(c) == null) {
                re.add(c);
            }
        }

        // check if we can attack another piece
        c = new Coordinate(x - 1, y - 1);
        if (c.isValid() && Board.getPiece(c) != null && !sameTeam(c)) {
            re.add(c);
        }
        c = new Coordinate(x - 1, y + 1);
        if (c.isValid() && Board.getPiece(c) != null && !sameTeam(c)) {
            re.add(c);
        }
        return re;
    }

    @Override
    public String getString() {
        return "\u265F";
    }
}
