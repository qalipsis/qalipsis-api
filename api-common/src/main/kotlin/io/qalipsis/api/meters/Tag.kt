///*
// * Copyright 2023 AERIS IT Solutions GmbH
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// * http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
// * or implied. See the License for the specific language governing
// * permissions and limitations under the License.
// */
//
//package io.qalipsis.api.meters
//
///**
// * Key/value pair representing a dimension used to classify and drill into measurements.
// *
// * @author Francisca Eze
// */
//class Tag(val key: String, val value: String) : Comparable<Tag> {
//    override fun compareTo(other: Tag): Int {
//        return this.key.compareTo(other.key)
//    }
//
//    companion object {
//        fun of(key: String, value: String): Tag {
//            return Tag(key, value)
//        }
//    }
//}