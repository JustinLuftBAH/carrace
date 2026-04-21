# Betting
Constraints: race.status==CREATED, user.balance>=amount
One bet per user per race (optional enforce)
On place: deduct or reserve balance
On finish: winnerCarId determines payouts
Payout: amount*2 default
