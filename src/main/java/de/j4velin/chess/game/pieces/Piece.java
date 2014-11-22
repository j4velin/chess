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

import java.util.List;

import de.j4velin.chess.game.Board;
import de.j4velin.chess.game.Coordinate;
import de.j4velin.chess.game.Game;

public abstract class Piece {

    /**
     * The current position
     */
    public Coordinate position;

    /**
     * The player ID who owns this piece
     */
    private final String ownerId;

    /**
     * Construct a new piece
     *
     * @param p the coordinate where this piece is located
     * @param o the ID of the player who owns this piece
     */
    Piece(final Coordinate p, final String o) {
        position = p;
        ownerId = o;
    }

    /**
     * Gets possible new positions for the current piece
     *
     * @return a list of possible new coordinates
     */
    public abstract List<Coordinate> getPossiblePositions();

    /**
     * Gets the player ID, to whom this piece belongs
     *
     * @return the owner id of this piece
     */
    public String getPlayerId() {
        return ownerId;
    }

    /**
     * Checks if this piece belongs to the same team as the destination piece
     *
     * @param destination the piece to check against
     * @return true, if both pieces belong to the same team or same player
     */
    boolean sameTeam(final Coordinate destination) {
        Piece p = Board.getPiece(destination);
        return p != null && Game.sameTeam(p.ownerId, ownerId);
    }

    /**
     * @return the unicode representation of this piece
     */
    public abstract String getString();

    @Override
    public String toString() {
        Coordinate c = new Coordinate(position.x, position.y, (4 - Board.getRotation()) % 4);
        return c.toString() + "," + ownerId + "," + getClass().getSimpleName();
    }
}