# WiFiMapper
WiFi signal strength mapping tool (JavaFX)

WiFi Mapper is a small JavaFX-based utility for visualizing wifi signal strength data. 

Data collection was performed using a modified version of [WigleWiFI](https://github.com/wiglenet/wigle-wifi-wardriving). (See below)

![](https://d2mxuefqeaa7sj.cloudfront.net/s_4645E43E6273408B49116C9F7ABF823095D3F27FA07844A72F00DD0BFE555C82_1527887413558_image.png)


## Build and Run Instructions (as of December 27, 2024)

### Prerequisites
1. **Operating System**: Windows 11
2. **JDKs Installed**:
   - **Liberica JDK 8 Full** (for JavaFX support with Java 8)
   - **Liberica JDK 23 Full** (optional, for modern compatibility)
3. **IDE**: Apache NetBeans 24
4. **Gradle**: Version 8.3 or newer
   - Download from [Gradle Downloads](https://gradle.org/releases/) and add it to your `PATH`.

### Setup Instructions
1. **Clone the Repository**:
   ```bash
   git clone https://github.com/yourusername/WiFiMapper.git
   cd WiFiMapper```
   
   
## Database Modification: Always Insert Location Observations

In `wigle-wifi-wardriving/wiglewifiwardriving/src/main/java/net/wigle/wigleandroid/db/DatabaseHelper.java`, under `// STEP 2: evaluate lat/lon diff`, modify the condition for inserting a new location so that it always evaluates to `true`. For example, change:

`if ( !blank && (isNew || bigChange || (! fastMode && changeWorthy )) ) {`

to:

`if ( true || !blank && (isNew || bigChange || (! fastMode && changeWorthy )) ) {`

This ensures that all valid location observations are stored, regardless of whether they meet the original evaluation criteria.




