package Server;

/**
 * Communication messages between server and client
 * when attached to Board and Profile objects.
 * @author Max Snelling
 * @version 5/5/20
 */
public enum Command {
	GET_GAMES,
	LOG_IN,
	NEW_GAME,
	JOIN_GAME,
	UPDATE,
	PASSWORD_CHECK,
	CORRECT,
	RECENT_GAMES,
	USERNAME_CHECK,
	NEW_PROFILE,
	NULL,
	GAME_END,
	LOG_OUT,
	LOGGED_OUT_CHECK
}