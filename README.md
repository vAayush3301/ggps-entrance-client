# ExamDesk

A secure, fullscreen, JavaFX-based Computer-Based Testing (CBT) application built for administering and taking exams with minimal distraction.

## Features

- Fullscreen exam mode
- Single test selection
- Timer with auto-submit
- Hosting and joining tests via LAN
- Scrollable test interface with custom question components
- Interactive dashboard for users
- Cross-platform support via packaged runtime (Java + JavaFX)
- Exported as standalone executable for easy distribution

## Installation

1. Download the installer (MSI/EXE) from the release folder.
2. Run the installer to install the application.
3. Launch the application from the start menu or desktop shortcut.
4. Make sure your network allows LAN connections if hosting/joining exams.

## Usage

- Select a test from the list and click **Attempt**.
- For administrators, you can host a test or manage existing tests.
- Timers automatically track the exam duration.
- The app ensures the exam runs in fullscreen to reduce distractions.

## Development

- Built with **Java 21**, **JavaFX 21**, **Gradle** for dependency management.
- JSON-based API communication with server.
- Custom ListView cells for interactive test items.
- Uses `Service` and `Task` for asynchronous API calls.

## License

This project is licensed under the MIT License. See `LICENSE.txt` for details.