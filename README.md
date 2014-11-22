4-Player Chess
=====
4-Player Chess offers a chess board with up to 64 pieces and four different game modes:

<ul>
<li>2-Player standard mode - normal chess game</li>
<li>2-Player extended mode - extended board, each player has 32 pieces</li>
<li>4-Player team mode - each player has the standard 16 pieces, but is allied with another player against two opponents</li>
<li>4-Player mode, no teams - deathmatch, each player has the standard 16 pieces and fight against 3 other players</li>
</ul>

There is no computer opponent implemented - you'll always play against other human beings!

Not yet implemented:
-----
<ul>
<li>Castling (http://en.wikipedia.org/wiki/Castling)</li>
<li>En passant (http://en.wikipedia.org/wiki/En_passant)</li>
<li>Promotion (http://en.wikipedia.org/wiki/Promotion_(chess))</li>
</ul>

Some notes:
-----
<ul>
<li>Even if your king is in check, you can still move every piece so that by the end of your move you're still in check (and probably checkmate after your opponents move)</li>
<li>There is no draw, but you can leave/cancel a match at any time</li>
<li>In 2-player extended mode, you have two kings, but you'll still lose if one of them is captured</li>
</ul>

Troubleshooting:
-----
This game uses the Google Play Game Services for matchfinding and other game related communication - make sure to have the latest Google Play Services installed and allow synchronization to be notified about match updates
