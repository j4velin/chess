package de.j4velin.chess.game;

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
public class Coordinate {

    public final int x, y;

    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Coordinate(int x, int y, int rotations) {
        int max = Board.extendedBoard ? 11 : 7;
        if (rotations != 0) {
            int tmp;
            for (int i = 0; i < rotations; i++) {
                tmp = x;
                x = y;
                y = max - tmp;
            }
        }
        this.x = x;
        this.y = y;
    }

    /**
     * Checks if the coordinate is on the board
     *
     * @return true, if the coordinate is valid
     */
    public boolean isValid() {
        if (Board.extendedBoard) {
            return (x >= 0 && y >= 0 && x <= 11 && y <= 11) &&
                    !(x <= 1 && y <= 1) && // bottom left
                    !(x >= 10 && y <= 1) && // bottom right
                    !(x <= 1 && y >= 10) && // upper left
                    !(x >= 10 && y >= 10); // upper right
        } else {
            return (x >= 0 && y >= 0 && x <= 7 && y <= 7);
        }
    }

    @Override
    public boolean equals(final Object other) {
        return other instanceof Coordinate && ((Coordinate) other).x == x &&
                ((Coordinate) other).y == y;
    }

    @Override
    public String toString() {
        return x + "," + y;
    }
}
