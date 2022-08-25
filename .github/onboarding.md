This step-by-step guide will support you on your way to your first pull request (PR) at Catrobat. To achieve this, we will guide you through the following steps:
1. Workspace Setup
2. Finding your first Ticket
3. Implementing your first ticket using Catrobat's Contribution Workflow

# 1. Workspace Setup
First, we will show you how to set up your working environment and verify the correctness of this setup.


## a. Installing the IDE and Tools
We recommend using [Android Studio](https://developer.android.com/studio) as all our contributors do. Please find the IDE version suitable for your operating system on the website and install it on your computer.

Additionally, you will need the Android Emulator to run our app. You can find more information about the Android Emulator and how to install and use it [here](https://developer.android.com/studio/run/emulator?gclid=Cj0KCQjwgO2XBhCaARIsANrW2X0Ii-xtaad3FcGEIRNjOhrX_ff40NQD3e4Zu4GOmeT3IHpzG-wPdW8aAqMGEALw_wcB&gclsrc=aw.ds).

We use Gradle for automating our build process. If you get an error message concerning Gradle (e.g., "gradle not found") please install the version specified in the [build.gradle file](https://github.com/Catrobat/Paintroid/blob/develop/build.gradle).


## b. Repository Setup
### (I) Git Setup
At Catrobat, we use Git to keep track of changes in our codebase. If you have not installed Git on your computer yet, please follow the [official guide to set up Git](https://docs.github.com/en/get-started/quickstart/set-up-git).

### (II) Catrobat's Forking Workflow
To enable the contribution of people like you, we decided to use a forking workflow. In a nutshell, this works as follows. First, everyone who wants to contribute creates (=forks) a personal copy of our repository (=fork). The contributor then makes changes on his fork and informs the community about the changes via a PR. A core contributor will review the changes in the PR. If the changes are accepted, the core contributor will merge the changes into the original repository of Catrobat.
If you are unfamiliar with Git or have not used it recently, the official guide about [forking a repository](https://docs.github.com/en/get-started/quickstart/fork-a-repo) is a good starting point.

### (III) Setting up your Fork
Now that you know how to work with Git, it is time to set up your fork by executing the following steps:
- [Fork](https://docs.github.com/en/get-started/quickstart/fork-a-repo#forking-a-repository) the [repository of Paintroid](https://github.com/Catrobat/Paintroid)
- Clone the fork:
  - [via Android Studio](https://www.geeksforgeeks.org/how-to-clone-android-project-from-github-in-android-studio/), or
  - [via Git Bash](https://docs.github.com/en/get-started/quickstart/fork-a-repo#cloning-your-forked-repository) and open it in your IDE manually
- [Configure synchronisation](https://docs.github.com/en/get-started/quickstart/fork-a-repo#configuring-git-to-sync-your-fork-with-the-original-repository) of your fork


## c. Setup Verification
To check if everything is set up correctly, you can now run the app in the emulator by choosing an emulator version and then pressing the "Run" button. If you can see our app on the emulator you are ready for the next step.



# 2. Finding your first Ticket


## a. Catrobat's Jira Workflow
At Catrobat, we use Jira to keep track of all issues (stories, tasks, and bugs) in our projects. You can find the Jira project of Paintroid [here](https://jira.catrob.at/projects/PAINTROID/issues/PAINTROID-446?filter=allopenissues).
If you click "Kanban Board" on the left menu in Jira, you will get an overview of what we are currently working on. You can see that different issues have different statuses (e.g., "Ready for Development"). The collection of all statuses makes up our Jira workflow that transparently shows the project's current state to every team member. You can find an overview of our Jira workflow if you click on an issue and the "(View Workflow)" next to the status field.
It is crucial to follow this workflow to keep the team informed about what you are currently working on.


## b. Choosing a suitable Ticket
We prepared a [pool of beginner tickets](https://jira.catrob.at/projects/PAINTROID/issues/?filter=allopenissues) for our newcomers that should be easy to implement. The tickets are marked with "Experience Level" BEGINNER, and you are free to choose any ticket from this pool that has not been assigned yet ("Status" = "Ready for Development"). Please also check the comments below to find out if another newcomer has already taken the issue!


## c. Informing the Community
As mentioned earlier, it is essential to keep the team updated. As you do not have the permissions for our Jira project yet, you cannot change the status of the issue you chose. Instead, please assign the ticket to yourself by commenting on it using the following template.

```diff
"I am starting to work on this issue. For the next 10 days, this ticket is assigned to me. If I am not able to create a pull request within 10 days, anybody else can take over this issue."
```

After you have finished your work and submitted a pull request, you have to use the following template to request a review from our community:

```diff
"@[Name of the responsible reviewer as mentioned in the beginner ticket] please review my pull request [Link to PR on GitHub (e.g., https://github.com/Catrobat/Catroid/pull/4580)]."
```

💡 In the upcoming steps, you will find the general workflow of the project. As you do not have permissions for Jira and Confluence yet, please skip all actions that involve Jira ticket status updates and additional information on Confluence!



# 3. Catrobat's Contribution Workflow
The general workflow of the project involves the following steps:
- Claim a Ticket
- Do the Work
- Submit the Changes

Please refer to our [contribution guide](https://github.com/Catrobat/Paintroid/blob/develop/.github/contributing.md) to receive step-by-step guidance throughout your contribution.
