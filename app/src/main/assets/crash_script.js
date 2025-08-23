// This script will populate the crash report HTML with data
// and handle the button actions

document.addEventListener('DOMContentLoaded', function() {
    if (window.crashData || window.AndroidCrashInterface) {
        let crashData;

        if (window.crashData) {
            crashData = window.crashData;
        } else if (window.AndroidCrashInterface) {
            try {
                crashData = JSON.parse(window.AndroidCrashInterface.getCrashData());
            } catch (e) {
                console.error("Error parsing crash data:", e);
                displayError();
                return;
            }
        } else {
            displayError();
            return;
        }

        const deviceInfoElement = document.getElementById('deviceInfo');
        if (deviceInfoElement && crashData.device) {
            deviceInfoElement.innerHTML = `
                <div>Brand: ${crashData.device.brand || 'Unknown'}</div>
                <div>Device: ${crashData.device.device || 'Unknown'}</div>
                <div>Model: ${crashData.device.model || 'Unknown'}</div>
                <div>Android Version: ${crashData.device.androidVersion || 'Unknown'}</div>
                <div>SDK: ${crashData.device.sdk || 'Unknown'}</div>
            `;
        }

        const crashTimeElement = document.getElementById('crashTime');
        if (crashTimeElement) {
            crashTimeElement.textContent = `Time: ${crashData.time || 'Unknown'}`;
        }

        const stackTraceElement = document.getElementById('stackTrace');
        if (stackTraceElement) {
            stackTraceElement.textContent = crashData.stackTrace || 'No stack trace available';
        }

        setupButtons();
    } else {
        displayError();
    }

    const detailsContent = document.querySelector('.details-content');
    if (detailsContent) {
        detailsContent.classList.add('fade-in');
    }
});

function setupButtons() {
    const shareButton = document.getElementById('btnShare');
    if (shareButton) {
        shareButton.addEventListener('click', function() {
            if (window.AndroidCrashInterface) {
                window.AndroidCrashInterface.shareCrashLog();
            }
        });
    }

    const restartButton = document.getElementById('btnRestart');
    if (restartButton) {
        restartButton.addEventListener('click', function() {
            if (window.AndroidCrashInterface) {
                window.AndroidCrashInterface.restartApp();
            }
        });
    }

    const buttons = document.querySelectorAll('.action-button');
    buttons.forEach(button => {
        button.addEventListener('touchstart', function() {
            this.style.opacity = '0.7';
        });

        button.addEventListener('touchend', function() {
            this.style.opacity = '1';
        });
    });
}

function displayError() {
    const deviceInfoElement = document.getElementById('deviceInfo');
    if (deviceInfoElement) {
        deviceInfoElement.innerHTML = '<div>Unable to load device information</div>';
    }

    const stackTraceElement = document.getElementById('stackTrace');
    if (stackTraceElement) {
        stackTraceElement.textContent = 'Error loading crash data. Please check the application logs.';
    }
}