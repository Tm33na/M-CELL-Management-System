
    

           
           
   function loadComplaintForm() {
    // Fetch the complaint form content using AJAX
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function() {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status === 200) {
                // Create a temporary container for the form content
                var tempDiv = document.createElement('div');
                tempDiv.innerHTML = xhr.responseText;

                // Find the form element within the loaded content
                var complaintForm = tempDiv.querySelector('.container');

                if (complaintForm) {
                    // Replace the content of the complaint-form-container with the complaint form
                    document.getElementById("complaint-form-container").innerHTML = complaintForm.outerHTML;
                } else {
                    console.error('Complaint form not found in the loaded content');
                }
            } else {
                console.error('Failed to load complaint form');
            }
        }
    };
    xhr.open('GET', 'ComplaintForm.html', true);
    xhr.send();
}



   function loadFeedbackForm() {
    // Fetch the complaint form content using AJAX
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function() {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status === 200) {
                // Create a temporary container for the form content
                var tempDiv = document.createElement('div');
                tempDiv.innerHTML = xhr.responseText;

                // Find the form element within the loaded content
                var FeedbackForm = tempDiv.querySelector('.container');

                if (FeedbackForm) {
                    // Replace the content of the complaint-form-container with the complaint form
                    document.getElementById("feedback-form-container").innerHTML = FeedbackForm.outerHTML;
                } else {
                    console.error('Feedback form not found in the loaded content');
                }
            } else {
                console.error('Failed to load Feedback form form');
            }
        }
    };
    xhr.open('GET', 'FeedbackForm.html', true);
    xhr.send();
}




function loadArchives() {
    var xhr = new XMLHttpRequest();
    xhr.open("GET", "RetrieveFilesServlet", true);
    xhr.onload = function () {
        if (xhr.status == 200) {
            // Parse the JSON response and display files with download links
            var files = JSON.parse(xhr.responseText);
            var archiveContent = "<ul>"; // Start building the HTML content
            files.forEach(function (file) {
                archiveContent += "<li>" + file.fileName + " - " + file.dateStored + " (<a href='DownloadFileServlet?fileId=" + file.id + "'>Download</a>)</li>";
            });
            archiveContent += "</ul>"; // Close the list
            // Open a popup window with the archives content
            var popupWindow = window.open("", "Archives", "width=600,height=400");
            // Write the HTML content to the popup window
            popupWindow.document.write("<html><head><title>Archives</title>  <link rel='stylesheet' href='styles.css'> </head><body>" + archiveContent + "</body></html>");
        } else {
            alert("Failed to fetch archives.");
        }
        // Redirect to the homepage
        window.location.href = "UserHome.html";
    };
    xhr.send();
}

    