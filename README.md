# XO Tournament Arena

XO Tournament Arena is a Java/XML Android Studio project for a polished tournament-based Tic-Tac-Toe game.

It transforms the classic X-O game into a complete tournament experience with multiple rounds, player customization, AI opponents, match history, statistics, achievements, sound effects, menu music, and a modern user interface.

## How to open

1. Open Android Studio.
2. Choose **Open**.
3. Select the `XO_Tournament_Arena` folder.
4. Let Gradle sync.
5. Run the app on an emulator or an Android phone.

## Main features

- Java activities with clean helper classes.
- XML layouts with rounded cards, custom buttons, and a modern UI.
- Splash screen before the main menu.
- Player name customization.
- Player vs Player mode.
- Player vs AI mode.
- AFK automatic mode.
- Easy, Medium, and Hard AI difficulties.
- Hard AI uses the minimax algorithm.
- Tournament scoring for 1, 3, 5, 10, or 15 rounds.
- Round transition overlay with sound effect.
- Custom confirmation overlay before leaving a tournament.
- Dark and light theme support.
- Sound effects using `SoundPool`.
- Menu background music using `MediaPlayer`.
- Automatic match history saving.
- Statistics screen based on saved tournaments.
- Achievements system.
- Personalized final feedback after each tournament.
- Settings screen for theme, sound effects, menu music, splash duration, and history reset.

## AI explanation

- **Easy:** chooses a random empty cell.
- **Medium:** checks if it can win immediately, then checks if it must block the opponent, otherwise chooses randomly.
- **Hard:** uses the minimax algorithm. It simulates possible future moves and chooses the move with the best guaranteed outcome.

## Saving and history explanation

`TournamentResult` implements `Serializable`.

`SaveManager` saves completed tournaments into internal app storage using `ObjectOutputStream` and loads them later with `ObjectInputStream`.

Instead of saving only one result, the app stores a match history so the user can review previous tournaments and generate statistics from them.

## Project structure

The project separates responsibilities across multiple classes:

- Activities handle the different screens of the application.
- `GameEngine` handles the board rules.
- `TournamentManager` handles rounds and scoring.
- `AIPlayer` handles computer moves.
- `AchievementManager` handles achievements.
- `FeedbackGenerator` creates final tournament feedback.
- `SaveManager` handles saved match history.
- `SoundManager` handles short sound effects.
- `MusicManager` handles menu background music.
- `TournamentResult` stores tournament data.

This separation keeps the code easier to understand, maintain, and present.

## Presentation notes

This project is intentionally beginner-friendly but still structured professionally. The app does not only execute a simple X-O match; it creates a full tournament experience with progression, feedback, statistics, history, sound, and customization.

The goal of the project is to show how a simple classic game can become a more complete Android application through clean architecture, thoughtful user experience, and well-organized Java/XML development.
