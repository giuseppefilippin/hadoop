# Soccer Championship Analysis with Hadoop

This project analyzes soccer championship data using Hadoop's MapReduce framework. The dataset includes match details, teams, and match outcomes. It is a collection of data about games in the Brazilian soccer championship containing data off all games from 2003 until 2023.

## Dataset Overview

The dataset used for this project includes the following columns:

- **ID**: Unique identifier for each match
- **Round**: The round number in the championship
- **Date**: The date the match took place
- **Hour**: The hour the match started
- **Day**: The day of the week of the match
- **Home Team**: The team playing at home
- **Visiting Team**: The team playing away
- **Home Team Formation**: The formation of the home team
- **Visiting Team Formation**: The formation of the visiting team
- **Home Team Coach**: The coach of the home team
- **Visiting Team Coach**: The coach of the visiting team
- **Winner**: The winner of the match
- **Stadium**: The stadium where the match was held
- **Home Team Score**: The score of the home team
- **Visiting Team Score**: The score of the visiting team
- **Home Team State**: The state or region of the home team
- **Visiting Team State**: The state or region of the visiting team
- **Winning State**: The state of the winning team

## Project Structure

The folders _basic_, _medium_ and _advanced_ under the _src_ contains the jobs organized by their complexity. The files represents the whole job, including both the _mapper_ and _reducer_ class, as well as the _driver_ class.

## Hadoop Jobs

### 1. Victories Per Team (WinsPerTeam.java)

This job counts the total victories achieved by each team in the period.

### 2. Average goals scored per a match (AvarageGoals.java)

This job calculates the avarage goals scored per a match in the period.

### 3. Games Per Stadium (GamesPerStadium.java)

This job counts the total games played on all stadiums in the period.

### 4. Home and visitor Team Victories (HomeVisitorWins.java)

This job counts the total victories to home and visitor teams in the period.

### 5. Average Goals per Stadium (AverageGoalsPerArena.java)

This job counts the avarage goals scored at each stadium in the period

### 6. Coach rivalries (CoachRilvary.java)

Given a pair of coachs that faced each other, this job counts the amount of wins of each coach in this dispute.

### 7. Matchup frequency (Matchups.java)

This job counts the amount of times that a pair of teams faced each other.

### 8. Derbies Stats (Derbies.java)

This job counts the amount of derby games of each state, the total amount of goals and the average goals per game.

### 9. Team Stats (TeamStats.java)

This job determines more advanced team stats, such as win rate, win balance and total games played by the team.

### 10. Champions of each year (Champions.java)

This job determines the champion of each year by calculating points of each team.

## Execution

To execute the jobs follow the steps:

1. **Clone the repository:**:

```bash
git clone https://github.com/FelipeABG/hadoop-mapreduce.git
cd hadoop-mapreduce
```

2. **Build the project using maven (make sure it is installed)**:

```bash
mvn package
```

3. **Execute the desired job using maven**:

```bash
mvn exec:java -Dexec.mainClass=pr.puc.mapreduce.<complexity>.<JobClassName> -Dexec.classpathScope=compile
```

Replace **complexity** with basic, medium, or advanced depending on the job's location, and **JobClassName** with the appropriate class name, such as WinsPerTeam, AverageGoals, GamesPerStadium, and so on.

After the execution, a folder called _output_ will be available. Inside it you will find the result of the job.

**OBS**: the advanced jobs (under the advanced folder) will produce a _result_ folder, as it uses the _output_ folder as intermediate step.
