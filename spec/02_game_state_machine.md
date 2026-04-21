# StateMachine
CREATED: allow join,bet
RUNNING: lock bets,accept position updates
FINISHED: stop updates,calculate payouts
Transitions:
startRace(): CREATED→RUNNING
finishRace(): RUNNING→FINISHED when position>=1000
Invalid transitions rejected.
