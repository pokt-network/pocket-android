<div align="center">
  <a href="https://www.pokt.network">
    <img src="https://pokt.network/wp-content/uploads/2018/12/Logo-488x228-px.png" alt="drawing" width="340"/>
  </a>
</div>
<h1 align="left">PocketAndroid</h1>
<h6 align="left">Official Android client to use with the Pocket Network</h6>
<div align="lef">
  <a  href="https://developer.android.com/docs">
    <img src="https://img.shields.io/badge/android-reference-green.svg"/>
  </a>
</div>

<h1 align="left">Overview</h1>
  <div align="left">
    <a  href="https://github.com/pokt-network/pocket-android/releases">
      <img src="https://img.shields.io/github/release-pre/pokt-network/pocket-android.svg"/>
    </a>
    <a href="https://circleci.com/gh/pokt-network/pocket-android/tree/master">
      <img src="https://circleci.com/gh/pokt-network/pocket-android/tree/master.svg?style=svg"/>
    </a>
    <a  href="https://github.com/pokt-network/pocket-android/pulse">
      <img src="https://img.shields.io/github/contributors/pokt-network/pocket-android.svg"/>
    </a>
    <a href="https://opensource.org/licenses/MIT">
      <img src="https://img.shields.io/badge/License-MIT-blue.svg"/>
    </a>
    <br >
    <a href="https://github.com/pokt-network/pocket-android/pulse">
      <img src="https://img.shields.io/github/last-commit/pokt-network/pocket-android.svg"/>
    </a>
    <a href="https://github.com/pokt-network/pocket-android/pulls">
      <img src="https://img.shields.io/github/issues-pr/pokt-network/pocket-android.svg"/>
    </a>
    <a href="https://github.com/pokt-network/pocket-android/issues">
      <img src="https://img.shields.io/github/issues-closed/pokt-network/pocket-android.svg"/>
    </a>
</div>

PocketAndroid wraps all of the tools a developer will need to begin interacting with a network. PocketAndroid contains 3 packages:

- `network.pocket:eth`: A library that allows your DApp to communicate to the Ethereum network.
- `network.pocket:aion`: A library that allows your DApp to communicate to the AION network.
- `network.pocket:core`: An implementation of the Pocket protocol that you can use to create your own plugin to interact with a blockchain of your choosing.

Before you can start using the library, you have to get a Developer ID by registering for MVP. [To learn how to register please click here.](https://pocket-network.readme.io/docs/how-to-participate#section-for-developers)

<h1 align="left">Requirements</h1>

You should have at least have a basic knowledge of blockchain technology and know your way around Java/Kotlin.

<h1 align="left">Installation</h1>

First you need to add the following Maven URL to your project, so the your root `build.gradle` file add the following:

```
allprojects {
    repositories {
        google()
        jcenter()
        maven {
          url 'https://dl.bintray.com/pokt-network/pocket-android'
        }
    }
}
```

Now, you will need to add either of the 3 packages within PocketAndroid to your module's `build.gradle` file, like this:

```
// Eth
implementation 'network.pocket:eth:0.0.2'

// Aion
implementation 'network.pocket:aion:0.0.3'

// Core
implementation 'network.pocket:core:0.0.3'
```

<h1 align="left">Usage</h1>

If you would like to know how to integrate PocketAndroid into your DApp, [visit our developer portal](https://pocket-network.readme.io) that has a lot of useful tutorials and material about the Pocket Network.

<h1 align="left">Contact Us</h1>

We have created a Discord server where you can meet with the Pocket team, as well as fellow App Developers, and Service Nodes. [Click here to join!](https://discord.gg/sarhfXP)
