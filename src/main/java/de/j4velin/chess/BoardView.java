package de.j4velin.chess;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import de.j4velin.chess.game.Board;
import de.j4velin.chess.game.Coordinate;
import de.j4velin.chess.game.Game;
import de.j4velin.chess.game.Player;
import de.j4velin.chess.game.pieces.Piece;
import de.j4velin.chess.util.Logger;

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
public class BoardView extends View {

    private Coordinate selection;
    private final Paint boardPaint = new Paint();
    private final Paint textPaint = new Paint();

    public BoardView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        textPaint.setTypeface(Typeface.createFromAsset(context.getAssets(), "Chess.ttf"));
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            int max = Board.extendedBoard ? 12 : 8;
            int x = (int) (event.getX() / getWidth() * max);
            int y = max - 1 - (int) (event.getY() / getWidth() * max);
            if (BuildConfig.DEBUG) Logger.log("selection: " + x + " " + y);
            Coordinate c = new Coordinate(x, y);
            if (c.isValid() && Board.getPiece(c) != null &&
                    Board.getPiece(c).getPlayerId().equals(Game.currentPlayer())) {
                selection = c;
                invalidate();
            } else {
                if (selection != null) { // we have a piece selected and clicked on a new position
                    if (Board.move(selection, c)) {
                        selection = null;
                        invalidate();
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void draw(final Canvas canvas) {
        super.draw(canvas);
        if (BuildConfig.DEBUG) Logger.log("onDraw");
        int max = Board.extendedBoard ? 12 : 8;
        float cellWidth = canvas.getWidth() / (float) max;
        Coordinate c;
        Piece p;
        textPaint.setTextSize(cellWidth);
        float textOffset = 0.15f * cellWidth;
        boardPaint.reset();
        for (int x = 0; x < max; x++) {
            for (int y = 0; y < max; y++) {
                c = new Coordinate(x, y);
                if (c.isValid()) {
                    if ((x + y) % 2 == 0) boardPaint.setColor(Color.GRAY);
                    else boardPaint.setColor(Color.DKGRAY);
                    drawCoordinate(c, canvas, cellWidth, boardPaint, max);
                    if (isInEditMode()) continue;
                    p = Board.getPiece(c);
                    if (p != null) {
                        textPaint.setColor(Game.getPlayerColor(p.getPlayerId()));
                        canvas.drawText(p.getString(), x * cellWidth,
                                (max - y) * cellWidth - textOffset, textPaint);
                    }
                }
            }
        }
        if (selection != null && (p = Board.getPiece(selection)) != null) {
            boardPaint.setAlpha(128);
            boardPaint.setColor(Color.CYAN);
            canvas.drawCircle(selection.x * cellWidth + cellWidth / 2,
                    (max - selection.y - 1) * cellWidth + cellWidth / 2, cellWidth / 2, boardPaint);
            textPaint.setColor(Game.getPlayerColor(p.getPlayerId()));
            canvas.drawText(p.getString(), selection.x * cellWidth,
                    (max - selection.y) * cellWidth - 10, textPaint);
            for (Coordinate possible : p.getPossiblePositions()) {
                drawCoordinate(possible, canvas, cellWidth, boardPaint, max);
            }
        }
        // print last moves
        boardPaint.setStyle(Paint.Style.STROKE);
        boardPaint.setStrokeWidth(5f);
        for (Player player : Game.players) {
            if (player.lastMove != null) {
                boardPaint.setColor(player.color);
                if (BuildConfig.DEBUG)
                    Logger.log("draw lastMove: " + player.lastMove.first.toString() + " to " +
                            player.lastMove.second.toString());
                drawCoordinate(player.lastMove.first, canvas, cellWidth, boardPaint, max);
                drawCoordinate(player.lastMove.second, canvas, cellWidth, boardPaint, max);
            }
        }
    }

    /**
     * Draws a rectangle at the given coordinate
     *
     * @param c         the coordinate
     * @param canvas    the canvas to draw to
     * @param cellWidth the widht of each cell
     * @param paint     the paint
     */
    private void drawCoordinate(final Coordinate c, final Canvas canvas, final float cellWidth, final Paint paint, int max) {
        canvas.drawRect(c.x * cellWidth, (max - c.y - 1) * cellWidth, (c.x + 1) * cellWidth,
                (max - c.y) * cellWidth, paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(Math.min(getMeasuredWidth(), getMeasuredHeight()),
                Math.min(getMeasuredWidth(), getMeasuredHeight()));
    }
}
