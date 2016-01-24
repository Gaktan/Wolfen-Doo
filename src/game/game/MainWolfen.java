package game.game;

public class MainWolfen {

	public static void main(String... args) {

		int width = 800;
		int height = 600;
		boolean fullScreen = false;
		boolean ignoreNext = false;

		for (int i = 0; i < args.length; i++) {
			if (ignoreNext) {
				ignoreNext = false;
				continue;
			}

			String arg = args[i].trim();

			if (arg.equals("-skipmenu")) {
				GameWolfen.SKIP_MENU = true;
				System.out.println("Skipping menu");
			}
			else if (arg.equals("-width")) {
				if (i > args.length - 1) {
					System.err.println("Error with parameter -width. Usage : -width value.");
					return;
				}

				String s_width = args[i + 1];

				try {
					width = Integer.parseInt(s_width);
					ignoreNext = true;
				} catch (NumberFormatException e) {
					System.err.println("Error with parameter -width. Usage : -width value. Value must be an integer");
					return;
				}
			}
			else if (arg.equals("-height")) {
				if (i > args.length - 1) {
					System.err.println("Error with parameter -height. Usage : -height value.");
					return;
				}

				String s_height = args[i + 1];

				try {
					height = Integer.parseInt(s_height);
					ignoreNext = true;
				} catch (NumberFormatException e) {
					System.err.println("Error with parameter -height. Usage : -height value. Value must be an integer");
					return;
				}
			}
			else if (arg.equals("-fullscreen")) {
				System.out.println("Fullscreen mode");
				fullScreen = true;
			}
			else {
				System.err.println("Unknown parameter " + arg + ".");
			}
		}

		new GameWolfen(width, height, fullScreen);
	}
}
