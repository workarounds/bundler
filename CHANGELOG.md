Change log
==========

Version 0.1.1 *(2016-01-26)*
----------------------------

* Reverted back to using `bundler-annotation` artifact
* Added `bundler-parceler` for Parceler library support
* Added argument `key` to `@Arg` and `@State` to specify custom key

Version 0.0.7 *(2016-01-26)*
----------------------------

* Replacing artifact `bundler-annotations` with `bundler`.
* Both `bundler` and `bundler-compiler` will have the same version number from now on. So you can use the following to define dependencies:
* Added `serializer` argument to `@Arg`
* Added `Serializer` which can be extened to define serialization of custom types that are not supported by default
* Added `ParcelListSerializer` which can be used to serialize a list of parcelables
