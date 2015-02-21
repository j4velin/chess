package de.j4velin.chess;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.j4velin.chess.game.Game;
import de.j4velin.chess.game.Player;
import de.j4velin.chess.util.Achievements;
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
public class GameFragment extends Fragment {

    private TextView turn;
    private View board;

    public String currentMatch;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        currentMatch = getArguments().getString("matchID");
        Main.gameFragment = this;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_game, container, false);
        turn = (TextView) v.findViewById(R.id.turn);
        board = v.findViewById(R.id.board);
        Game.UI = this;
        updateTurn();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        Game.save(getActivity());
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        return ((Main) getActivity()).optionsItemSelected(item);
    }

    public void update(boolean gameOver) {
        if (BuildConfig.DEBUG)
            Logger.log(" UI:update() " + gameOver + " board=null?" + (board == null));
        if (board == null) return;
        board.invalidate();
        if (!gameOver) updateTurn();
    }

    public void gameOver(boolean win) {
        if (turn == null) return;
        turn.setText("Game Over!\n" +
                (win ? "Congratulation, you've won the match!" : "Sorry, you've lost the match!"));
        if (win && getActivity() != null) {
            Achievements.checkAchievements(((Main) getActivity()).getGC(), getActivity());
        }
    }

    public void updateTurn() {
        if (BuildConfig.DEBUG) Logger.log(" UI:updateTurn() turn=null?" + (turn == null));
        if (turn == null) return;
        if (Game.isGameOver()) {
            gameOver(Game.getWinnerTeam() == Game.getPlayer(Game.myPlayerId).team);
        } else {
            StringBuilder sb = new StringBuilder();
            String current = Game.players[Game.turns % Game.players.length].id;
            if (BuildConfig.DEBUG) Logger.log(" current player: " + current);
            for (Player p : Game.players) {
                if (BuildConfig.DEBUG)
                    Logger.log(" UI:updateTurn() player " + p.id + " " + p.name + " " + p.team);
                sb.append("<font color='")
                        .append(String.format("#%06X", (0xFFFFFF & Game.getPlayerColor(p.id))))
                        .append("'>");
                if (p.id.equals(current)) sb.append("-> ");
                if (Game.match.mode == Game.MODE_4_PLAYER_TEAMS) {
                    sb.append(p.name).append(" [").append(p.team).append("]</font><br />");
                } else {
                    sb.append(p.name).append("</font><br />");
                }
            }
            sb.delete(sb.lastIndexOf("<br />"), sb.length());
            turn.setText(Html.fromHtml(sb.toString()));
        }
    }


}
