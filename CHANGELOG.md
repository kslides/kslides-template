# Change Log 
All notable changes to this template will be documented in this file.

## [1.2.0] 5/18/2022

### Added
* Add `plotly_version=0.5.1-dev-4` to _gradle.properties_
* Add `maven { url = 'https://repo.kotlin.link' }` to `repositories{}` in _build.gradle_
* Add `implementation "space.kscience:plotlykt-core:$plotly_version"` to `dependencies{}` in _build.gradle_

### Updated
* Update `kslides_version=0.10.0` in _gradle.properties_
* Update `jvmTarget = '11'` in _compileKotlin.kotlinOptions_ and _compileTestKotlin.kotlinOptions_

## [1.1.5] 5/14/2022

### Updated
* Update `kslides_version=0.9.0` in _gradle.properties_ 