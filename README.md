# NeonLightBackgroundLayout
A Layout That helps you create a glowing neon light effect as the background of it's children.

[![](https://jitpack.io/v/mr-sarsarabi/NeonLightBackgroundLayout.svg)](https://jitpack.io/#mr-sarsarabi/NeonLightBackgroundLayout)

#### Gradle:
**Step 1.** Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
**Step 2.** Add the dependency

	dependencies {
	        implementation 'com.github.mr-sarsarabi:NeonLightBackgroundLayout:1.0.0'
	}

#### Maven:


**Step 1.** Add the JitPack repository to your build file

	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>

**Step 2.** Add the dependency

	<dependency>
	    <groupId>com.github.mr-sarsarabi</groupId>
	    <artifactId>NeonLightBackgroundLayout</artifactId>
	    <version>v1.0.0</version>
	</dependency>


### Usage

Look at sample app for more details.

    <com.MirageStudios.library.NeonBackgroundLayout
            android:layout_width="250dp"
            android:layout_height="250dp"
            android:layout_gravity="center"
            android:padding="48dp"
            app:neon_bottomPadding="4dp"
            app:neon_cornerRadius="16dp"
            app:neon_innerBackgroundColor="#48000000"
            app:neon_innerBackgroundPadding="6dp"
            app:neon_leftPadding="4dp"
            app:neon_rightPadding="4dp"
            app:neon_shadowColor="#FFB300"
            app:neon_shadowMultiplier="1.5"
            app:neon_strokeColor="#FFB300"
            app:neon_strokeWidth="3dp"
            app:neon_style="style_stroke_with_inner_background_and_shadow"
            app:neon_topPadding="4dp">
            
    </com.MirageStudios.library.NeonBackgroundLayout>
