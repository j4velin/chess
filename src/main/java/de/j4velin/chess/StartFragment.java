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
package de.j4velin.chess;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchBuffer;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;

import java.util.ArrayList;
import java.util.List;

import de.j4velin.chess.game.Game;
import de.j4velin.chess.util.Logger;

public class StartFragment extends Fragment {

    private static MatchesAdapter myTurns;
    private static MatchesAdapter pending;

    static int LAST_SELECTED_MATCH_MODE;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (BuildConfig.DEBUG) Logger.log("StartFragment onCreate");
        Main.startFragment = this;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_start, container, false);
        v.findViewById(R.id.start_game).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final Dialog d = new Dialog(getActivity());
                d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                d.setContentView(R.layout.mode);
                ((RadioGroup) d.findViewById(R.id.game_mode))
                        .setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(final RadioGroup radioGroup, int item) {
                                LAST_SELECTED_MATCH_MODE =
                                        Integer.parseInt((String) d.findViewById(item).getTag());
                                int other_player =
                                        LAST_SELECTED_MATCH_MODE == Game.MODE_2_PLAYER_4_SIDES ||
                                                LAST_SELECTED_MATCH_MODE ==
                                                        Game.MODE_2_PLAYER_2_SIDES ? 1 : 3;
                                Intent intent = Games.TurnBasedMultiplayer
                                        .getSelectOpponentsIntent(((Main) getActivity()).getGC(),
                                                other_player, other_player, true);
                                getActivity()
                                        .startActivityForResult(intent, Main.RC_SELECT_PLAYERS);
                                d.dismiss();
                            }
                        });
                d.show();
            }
        });
        v.findViewById(R.id.inbox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Intent intent =
                        Games.TurnBasedMultiplayer.getInboxIntent(((Main) getActivity()).getGC());
                getActivity().startActivityForResult(intent, Main.RC_MATCH_HISTORY);
            }
        });
        ListView active = (ListView) v.findViewById(R.id.active);
        myTurns = new MatchesAdapter();
        active.setAdapter(myTurns);
        active.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, int position, long id) {
                TurnBasedMatch m = (TurnBasedMatch) myTurns.getItem(position);
                if (m.getData() == null) {
                    Game.newGame(m, ((Main) getActivity()).getGC());
                } else {
                    if (!Game.load(m.getData(), m, ((Main) getActivity()).getGC())) {
                        ((Main) getActivity()).updateApp();
                        return;
                    }
                }
                ((Main) getActivity()).startGame(m.getMatchId());
            }
        });
        ListView pendingList = (ListView) v.findViewById(R.id.pending);
        pending = new MatchesAdapter();
        pendingList.setAdapter(pending);
        pendingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, int position, long id) {
                TurnBasedMatch m = (TurnBasedMatch) pending.getItem(position);
                if (m.getData() == null) {
                    Game.newGame(m, ((Main) getActivity()).getGC());
                } else {
                    if (!Game.load(m.getData(), m, ((Main) getActivity()).getGC())) {
                        ((Main) getActivity()).updateApp();
                        return;
                    }
                }
                ((Main) getActivity()).startGame(m.getMatchId());
            }
        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
        if (BuildConfig.DEBUG) Logger.log("StartFragment onResume, isConnected: " +
                (((Main) getActivity()).getGC().isConnected()));
        if (((Main) getActivity()).getGC().isConnected()) loadMatches();
    }

    public void loadMatches() {
        if (BuildConfig.DEBUG) Logger.log("StartFramgnet.loadMatches");
        if (getView() == null) return; // not visible
        getView().findViewById(R.id.start_game).setVisibility(View.VISIBLE);
        getView().findViewById(R.id.inbox).setVisibility(View.VISIBLE);
        Games.TurnBasedMultiplayer.loadMatchesByStatus(((Main) getActivity()).getGC(),
                new int[]{TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN,
                        TurnBasedMatch.MATCH_TURN_STATUS_THEIR_TURN})
                .setResultCallback(new ResultCallback<TurnBasedMultiplayer.LoadMatchesResult>() {
                    @Override
                    public void onResult(final TurnBasedMultiplayer.LoadMatchesResult result) {
                        myTurns.setMatches(result.getMatches().getMyTurnMatches());
                        pending.setMatches(result.getMatches().getTheirTurnMatches());
                        result.release();
                    }
                });
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.start, menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_reload:
                if (((Main) getActivity()).getGC().isConnected()) {
                    loadMatches();
                    Toast.makeText(getActivity(), "Loading matches...", Toast.LENGTH_SHORT).show();
                } else {
                    ((Main) getActivity()).getGC().connect();
                }
                return true;
            case R.id.action_achievement:
                if (((Main) getActivity()).getGC().isConnected()) {
                    startActivityForResult(Games.Achievements
                            .getAchievementsIntent(((Main) getActivity()).getGC()), 1);
                } else {
                    ((Main) getActivity()).getGC().connect();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class MatchesAdapter extends BaseAdapter {

        private List<TurnBasedMatch> matches;
        private final LayoutInflater inflater;

        private MatchesAdapter() {
            inflater = LayoutInflater.from(getActivity());
        }

        @Override
        public int getCount() {
            return matches == null ? 0 : matches.size();
        }

        @Override
        public Object getItem(int position) {
            return matches.get(position);
        }

        @Override
        public long getItemId(int position) {
            return matches.get(position).getMatchNumber();
        }

        @Override
        public View getView(int position, View convertView, final ViewGroup viewGroup) {
            final ViewHolder holder;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.match, null);
                holder = new ViewHolder();
                holder.team1 = (TextView) convertView.findViewById(R.id.team1);
                holder.team2 = (TextView) convertView.findViewById(R.id.team2);
                holder.time = (TextView) convertView.findViewById(R.id.time);
                holder.delete = (ImageView) convertView.findViewById(R.id.delete);
                holder.desc = (TextView) convertView.findViewById(R.id.desc);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final TurnBasedMatch m = matches.get(position);
            List<String> names = new ArrayList<String>(4);
            for (String p : m.getParticipantIds()) {
                names.add(m.getParticipant(p).getDisplayName());
            }

            if (names.size() == 3) {
                // can happen if there isn't even one turn yet
                Games.TurnBasedMultiplayer
                        .cancelMatch(((Main) getActivity()).getGC(), m.getMatchId());
                loadMatches();
            } else {
                holder.desc.setText(m.getDescription());
                if (names.size() <= 2) {
                    holder.team1.setText(names.get(0));
                    holder.team2.setText(names.size() > 1 ? names.get(1) : "?");
                } else if (m.getVariant() == Game.MODE_4_PLAYER_TEAMS) {
                    holder.team1.setText("Team 1: " +
                            names.get(0) + ", " + names.get(1));
                    holder.team2.setText("Team 2: " +
                            names.get(2) + ", " + names.get(3));
                } else { // 4 player, no teams
                    holder.team1.setText(names.get(0) + ", " + names.get(1) + ",");
                    holder.team2.setText(names.get(2) + ", " + names.get(3));
                }
            }
            holder.time.setText(DateUtils.getRelativeTimeSpanString(m.getLastUpdatedTimestamp()));
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    new AlertDialog.Builder(getActivity())
                            .setMessage("Do you really want to leave this match?")
                            .setPositiveButton(android.R.string.yes,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(final DialogInterface dialogInterface, int i) {
                                            if (m.getStatus() ==
                                                    TurnBasedMatch.MATCH_STATUS_COMPLETE) {
                                                Games.TurnBasedMultiplayer.dismissMatch(
                                                        ((Main) getActivity()).getGC(),
                                                        m.getMatchId());
                                            } else if (m.getTurnStatus() ==
                                                    TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN) {
                                                String next;
                                                try {
                                                    String[] s = new String(m.getData()).split(":");
                                                    int index = (Integer.parseInt(s[0]) + 1) %
                                                            (m.getParticipants().size() +
                                                                    m.getAvailableAutoMatchSlots());
                                                    if (m.getParticipants().size() > index) next =
                                                            m.getParticipants().get(index)
                                                                    .getParticipantId();
                                                    else next = null;

                                                    Games.TurnBasedMultiplayer.leaveMatchDuringTurn(
                                                            ((Main) getActivity()).getGC(),
                                                            m.getMatchId(), next);
                                                } catch (NullPointerException npe) {
                                                    // can happen if there isn't even one turn yet
                                                    Games.TurnBasedMultiplayer.cancelMatch(
                                                            ((Main) getActivity()).getGC(),
                                                            m.getMatchId());
                                                }
                                            } else {
                                                Games.TurnBasedMultiplayer
                                                        .leaveMatch(((Main) getActivity()).getGC(),
                                                                m.getMatchId());
                                            }
                                            loadMatches();
                                            dialogInterface.dismiss();
                                        }
                                    }).setNegativeButton(android.R.string.no,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(final DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).create().show();
                }
            });

            return convertView;
        }

        private void setMatches(final TurnBasedMatchBuffer m) {
            matches = new ArrayList<TurnBasedMatch>(m.getCount());
            for (TurnBasedMatch match : m) {
                matches.add(match.freeze());
            }
            m.release();
            notifyDataSetChanged();
        }
    }

    private class ViewHolder {
        TextView team1, team2, time, desc;
        ImageView delete;
    }
}
