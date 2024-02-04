# Civil Advocacy App

## Overview

Civil Advocacy is an Android app designed to help users find and interact with political officials representing their current location or a specified location. The app utilizes Location Services, Internet, Google APIs, Images, Picasso Library, Implicit Intents, and TextView Links to provide a seamless experience.

## App Highlights

- Acquire and display an interactive list of political officials.
- Use Android Fused Location Provider services to determine the user's current location.
- Fetch government official data from the Google Civic Information API via REST service and JSON results.
- Different layout for landscape orientation for certain activities.
- Clicking on an official's list entry opens a detailed view of the representative.
- Clicking on the photo of an official displays a larger version in the Photo Activity.
- About activity shows application information (Author, Copyright data & Version).

## Required Permissions

- ACCESS_FINE_LOCATION
- ACCESS_COARSE_LOCATION
- INTERNET

## API Key

To use the Google Civic Information API, you need to obtain an API key. Follow these steps to acquire your key:

1. Visit the [Google Cloud Console](https://console.cloud.google.com/).
2. Create a new project or select an existing one.
3. Navigate to the "APIs & Services" > "Credentials" section.
4. Click on "Create Credentials" and select "API Key".
5. Copy your API key.
   
## Application Behavior Diagrams

### 1. Main Activity

- Displays a RecyclerView list of government officials.
- Provides options menu items for "about" information and manual location entry.
- Clicking on an official's entry opens a new activity containing detailed information on the selected individual.

### 2. Official Activity

- Displays contact information of the selected official.
- Background color based on the official's political party.
- Party logo for Democratic & Republican officials.

### 3. Photo Detail Activity

- Shows a full-sized image of the official.
- Background color based on the official's political party.
- Party logo for Democratic & Republican officials.

### 4. About Activity

- Displays application information including author, copyright data, and version.

## Development Plan

1. Create the base app with MainActivity, Official Class, RecyclerView Adapter, ViewHolder, and About Activity.
2. Implement location code and dummy data for testing.
3. Add Official Activity with social media and contact information.
4. Integrate Google Civic Information API for real data.
5. Test the app thoroughly against all requirements.

## Usage

1. Clone the repository.
2. Add your Google Civic Information API key to the project.
3. Open the project in Android Studio.
4. Build and run the app on an Android emulator or device.

