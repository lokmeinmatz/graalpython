/*
 * Copyright (c) 2023, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * The Universal Permissive License (UPL), Version 1.0
 *
 * Subject to the condition set forth below, permission is hereby granted to any
 * person obtaining a copy of this software, associated documentation and/or
 * data (collectively the "Software"), free of charge and under any and all
 * copyright rights in the Software, and any and all patent rights owned or
 * freely licensable by each licensor hereunder covering either (i) the
 * unmodified Software as contributed to or provided by such licensor, or (ii)
 * the Larger Works (as defined below), to deal in both
 *
 * (a) the Software, and
 *
 * (b) any piece of software and/or hardware listed in the lrgrwrks.txt file if
 * one is included with the Software each a "Larger Work" to which the Software
 * is contributed by such licensors),
 *
 * without restriction, including without limitation the rights to copy, create
 * derivative works of, display, perform, and distribute the Software and make,
 * use, sell, offer for sale, import, export, have made, and have sold the
 * Software and the Larger Work(s), and to sublicense the foregoing rights on
 * either these or other terms.
 *
 * This license is subject to the following condition:
 *
 * The above copyright notice and either this complete permission notice or at a
 * minimum a reference to the UPL must be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.oracle.graal.python.builtins.objects.cext.hpy.jni;

import com.oracle.graal.python.builtins.objects.cext.hpy.GraalHPyContext.LLVMType;
import com.oracle.graal.python.builtins.objects.cext.hpy.HPyMode;
import com.oracle.graal.python.runtime.PythonOptions;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

/**
 * Represents a native function pointer that will be called using an appropriate JNI trampoline
 * function depending on the {@link #signature} and the {@link #mode} enum.
 */
@ExportLibrary(InteropLibrary.class)
public final class GraalHPyJNIFunctionPointer implements TruffleObject {
    final long pointer;
    final LLVMType signature;

    /**
     * Function pointers created through {@code HPyModule_Create} or {@code HPyType_FromSpec}
     * remembers if the context that created it was in debug mode. Depending on this flag, we decide
     * which trampolines (universal or debug) we need to use. For reference: In CPython this is
     * implicitly given by the fact that the HPy context is stored in a C global variable
     * {@code _ctx_for_trampolines}.
     */
    final HPyMode mode;

    public GraalHPyJNIFunctionPointer(long pointer, LLVMType signature, HPyMode mode) {
        assert !PythonOptions.WITHOUT_JNI;
        this.pointer = pointer;
        this.signature = signature;
        this.mode = mode;
    }

    @ExportMessage
    @SuppressWarnings("static-method")
    boolean isExecutable() {
        return true;
    }

    @ExportMessage
    static final class Execute {

        @Specialization(guards = "receiver.signature == cachedSignature", limit = "1")
        static Object doCached(GraalHPyJNIFunctionPointer receiver, Object[] arguments,
                        @CachedLibrary(limit = "1") InteropLibrary interopLibrary,
                        @Cached("receiver.signature") LLVMType cachedSignature,
                        @Cached(parameters = "receiver.signature") GraalHPyJNIConvertArgNode convertArgNode) {
            // Make it explicit, that we cannot to JNI calls if WITHOUT_JNI is true.
            if (PythonOptions.WITHOUT_JNI) {
                throw CompilerDirectives.shouldNotReachHere();
            }
            return switch (receiver.mode) {
                case MODE_UNIVERSAL -> callUniversal(receiver, cachedSignature, convertHPyContext(arguments), arguments, interopLibrary, convertArgNode);
                case MODE_DEBUG -> callDebug(receiver, cachedSignature, convertHPyDebugContext(arguments), arguments, interopLibrary, convertArgNode);
                case MODE_TRACE -> callUniversal(receiver, cachedSignature, convertHPyTraceContext(arguments), arguments, interopLibrary, convertArgNode);
                default -> throw CompilerDirectives.shouldNotReachHere("unsupported HPy mode");
            };
        }

        /**
         * Uses the appropriate trampoline to call the native function pointer.
         */
        private static long callUniversal(GraalHPyJNIFunctionPointer receiver, LLVMType signature, long ctx, Object[] arguments,
                        InteropLibrary interopLibrary, GraalHPyJNIConvertArgNode convertArgNode) {
            switch (signature) {
                case HPyModule_init:
                    return GraalHPyJNITrampolines.executeModuleInit(receiver.pointer);
                case HPyModule_create:
                    return GraalHPyJNITrampolines.executeModcreate(receiver.pointer, ctx, convertArgNode.execute(arguments, 1));
                case HPyFunc_noargs:
                    return GraalHPyJNITrampolines.executeNoargs(receiver.pointer, ctx, convertArgNode.execute(arguments, 1));
                case HPyFunc_unaryfunc:
                    return GraalHPyJNITrampolines.executeUnaryfunc(receiver.pointer, ctx, convertArgNode.execute(arguments, 1));
                case HPyFunc_getiterfunc:
                    return GraalHPyJNITrampolines.executeGetiterfunc(receiver.pointer, ctx, convertArgNode.execute(arguments, 1));
                case HPyFunc_iternextfunc:
                    return GraalHPyJNITrampolines.executeIternextfunc(receiver.pointer, ctx, convertArgNode.execute(arguments, 1));
                case HPyFunc_reprfunc:
                    return GraalHPyJNITrampolines.executeReprfunc(receiver.pointer, ctx, convertArgNode.execute(arguments, 1));
                case HPyFunc_lenfunc:
                    return GraalHPyJNITrampolines.executeLenfunc(receiver.pointer, ctx, convertArgNode.execute(arguments, 1));
                case HPyFunc_hashfunc:
                    return GraalHPyJNITrampolines.executeHashfunc(receiver.pointer, ctx, convertArgNode.execute(arguments, 1));
                case HPyFunc_binaryfunc:
                    return GraalHPyJNITrampolines.executeBinaryfunc(receiver.pointer, ctx, convertArgNode.execute(arguments, 1), convertArgNode.execute(arguments, 2));
                case HPyFunc_o:
                    return GraalHPyJNITrampolines.executeO(receiver.pointer, ctx, convertArgNode.execute(arguments, 1), convertArgNode.execute(arguments, 2));
                case HPyFunc_getter:
                    return GraalHPyJNITrampolines.executeGetter(receiver.pointer, ctx, convertArgNode.execute(arguments, 1), convertArgNode.execute(arguments, 2));
                case HPyFunc_getattrfunc:
                    return GraalHPyJNITrampolines.executeGetattrfunc(receiver.pointer, ctx, convertArgNode.execute(arguments, 1), convertArgNode.execute(arguments, 2));
                case HPyFunc_getattrofunc:
                    return GraalHPyJNITrampolines.executeGetattrofunc(receiver.pointer, ctx, convertArgNode.execute(arguments, 1), convertArgNode.execute(arguments, 2));
                case HPyFunc_ssizeargfunc:
                    return GraalHPyJNITrampolines.executeSsizeargfunc(receiver.pointer, ctx, convertArgNode.execute(arguments, 1), convertArgNode.execute(arguments, 2));
                case HPyFunc_traverseproc:
                    return GraalHPyJNITrampolines.executeTraverseproc(receiver.pointer, ctx, convertArgNode.execute(arguments, 1), convertArgNode.execute(arguments, 2));
                case HPyFunc_varargs:
                    return GraalHPyJNITrampolines.executeVarargs(receiver.pointer, ctx, convertArgNode.execute(arguments, 1), convertArgNode.execute(arguments, 2),
                                    convertArgNode.execute(arguments, 3));
                case HPyFunc_ternaryfunc:
                    return GraalHPyJNITrampolines.executeTernaryfunc(receiver.pointer, ctx, convertArgNode.execute(arguments, 1), convertArgNode.execute(arguments, 2),
                                    convertArgNode.execute(arguments, 3));
                case HPyFunc_descrgetfunc:
                    return GraalHPyJNITrampolines.executeDescrgetfunc(receiver.pointer, ctx, convertArgNode.execute(arguments, 1), convertArgNode.execute(arguments, 2),
                                    convertArgNode.execute(arguments, 3));
                case HPyFunc_ssizessizeargfunc:
                    return GraalHPyJNITrampolines.executeSsizessizeargfunc(receiver.pointer, ctx, convertArgNode.execute(arguments, 1), convertArgNode.execute(arguments, 2),
                                    convertArgNode.execute(arguments, 3));
                case HPyFunc_keywords:
                    return GraalHPyJNITrampolines.executeKeywords(receiver.pointer, ctx, convertArgNode.execute(arguments, 1), convertArgNode.execute(arguments, 2),
                                    convertArgNode.execute(arguments, 3), convertArgNode.execute(arguments, 4));
                case HPyFunc_inquiry:
                    return GraalHPyJNITrampolines.executeInquiry(receiver.pointer, ctx, convertArgNode.execute(arguments, 1));
                case HPyFunc_ssizeobjargproc:
                    return GraalHPyJNITrampolines.executeSsizeobjargproc(receiver.pointer, ctx, convertArgNode.execute(arguments, 1), (long) arguments[2], convertArgNode.execute(arguments, 3));
                case HPyFunc_initproc:
                    return GraalHPyJNITrampolines.executeInitproc(receiver.pointer, ctx, convertArgNode.execute(arguments, 1), convertArgNode.execute(arguments, 2), (long) arguments[3],
                                    convertArgNode.execute(arguments, 4));
                case HPyFunc_ssizessizeobjargproc:
                    return GraalHPyJNITrampolines.executeSsizessizeobjargproc(receiver.pointer, ctx, convertArgNode.execute(arguments, 1), (long) arguments[2], (long) arguments[3],
                                    convertArgNode.execute(arguments, 4));
                case HPyFunc_setter:
                    return GraalHPyJNITrampolines.executeSetter(receiver.pointer, ctx, convertArgNode.execute(arguments, 1), convertArgNode.execute(arguments, 2),
                                    convertArgNode.execute(arguments, 3));
                case HPyFunc_setattrfunc:
                    return GraalHPyJNITrampolines.executeSetattrfunc(receiver.pointer, ctx, convertArgNode.execute(arguments, 1), convertArgNode.execute(arguments, 2),
                                    convertArgNode.execute(arguments, 3));
                case HPyFunc_objobjargproc:
                    return GraalHPyJNITrampolines.executeObjobjargproc(receiver.pointer, ctx, convertArgNode.execute(arguments, 1), convertArgNode.execute(arguments, 2),
                                    convertArgNode.execute(arguments, 3));
                case HPyFunc_descrsetfunc:
                    return GraalHPyJNITrampolines.executeDescrsetfunc(receiver.pointer, ctx, convertArgNode.execute(arguments, 1), convertArgNode.execute(arguments, 2),
                                    convertArgNode.execute(arguments, 3));
                case HPyFunc_setattrofunc:
                    return GraalHPyJNITrampolines.executeSetattrofunc(receiver.pointer, ctx, convertArgNode.execute(arguments, 1), convertArgNode.execute(arguments, 2),
                                    convertArgNode.execute(arguments, 3));
                case HPyFunc_freefunc:
                    GraalHPyJNITrampolines.executeFreefunc(receiver.pointer, ctx, convertArgNode.execute(arguments, 1));
                    return 0;
                case HPyFunc_richcmpfunc:
                    return GraalHPyJNITrampolines.executeRichcmpfunc(receiver.pointer, ctx, convertArgNode.execute(arguments, 1), convertArgNode.execute(arguments, 2), (int) arguments[3]);
                case HPyFunc_objobjproc:
                    return GraalHPyJNITrampolines.executeObjobjproc(receiver.pointer, ctx, convertArgNode.execute(arguments, 1), convertArgNode.execute(arguments, 2));
                case HPyFunc_getbufferproc:
                    return GraalHPyJNITrampolines.executeGetbufferproc(receiver.pointer, ctx, convertArgNode.execute(arguments, 1), convertArgNode.execute(arguments, 2), (int) arguments[3]);
                case HPyFunc_releasebufferproc:
                    GraalHPyJNITrampolines.executeReleasebufferproc(receiver.pointer, ctx, convertArgNode.execute(arguments, 1), convertArgNode.execute(arguments, 2));
                    return 0;
                case HPyFunc_destroyfunc:
                    GraalHPyJNITrampolines.executeDestroyfunc(receiver.pointer, convertPointer(arguments[0], interopLibrary));
                    return 0;
                case HPyFunc_destructor:
                    GraalHPyJNITrampolines.executeDestructor(receiver.pointer, ctx, convertArgNode.execute(arguments, 1));
                    return 0;
            }
            throw CompilerDirectives.shouldNotReachHere();
        }

        /**
         * When we are in debug mode, we need to use different trampolines for calling the HPy
         * extension functions because object parameters (that will become handles) will be wrapped
         * in debug handles ({@code DHPy}) and, vice versa, object return values need to be
         * unwrapped. This un/-wrapping is done by the trampoline via calling {@code DHPy_open} and
         * {@code DHPy_unwrap}.
         */
        private static long callTrace(GraalHPyJNIFunctionPointer receiver, LLVMType signature, Object[] arguments,
                        InteropLibrary interopLibrary, GraalHPyJNIConvertArgNode convertArgNode) {
            switch (signature) {
                case HPyModule_init:
                    // there is no difference to the universal mode
                    return GraalHPyJNITrampolines.executeModuleInit(receiver.pointer);
                case HPyModule_create:
                    return GraalHPyJNITrampolines.executeDebugModcreate(receiver.pointer, convertHPyTraceContext(arguments), convertArgNode.execute(arguments, 1));
                case HPyFunc_noargs:
                    return GraalHPyJNITrampolines.executeDebugNoargs(receiver.pointer, convertHPyTraceContext(arguments), convertArgNode.execute(arguments, 1));
                case HPyFunc_unaryfunc:
                    return GraalHPyJNITrampolines.executeDebugUnaryfunc(receiver.pointer, convertHPyTraceContext(arguments), convertArgNode.execute(arguments, 1));
                case HPyFunc_getiterfunc:
                    return GraalHPyJNITrampolines.executeDebugGetiterfunc(receiver.pointer, convertHPyTraceContext(arguments), convertArgNode.execute(arguments, 1));
                case HPyFunc_iternextfunc:
                    return GraalHPyJNITrampolines.executeDebugIternextfunc(receiver.pointer, convertHPyTraceContext(arguments), convertArgNode.execute(arguments, 1));
                case HPyFunc_reprfunc:
                    return GraalHPyJNITrampolines.executeDebugReprfunc(receiver.pointer, convertHPyTraceContext(arguments), convertArgNode.execute(arguments, 1));
                case HPyFunc_lenfunc:
                    return GraalHPyJNITrampolines.executeDebugLenfunc(receiver.pointer, convertHPyTraceContext(arguments), convertArgNode.execute(arguments, 1));
                case HPyFunc_hashfunc:
                    // HPy_ssize_t (*HPyFunc_lenfunc)(HPyContext *ctx, HPy);
                    // HPy_hash_t (*HPyFunc_hashfunc)(HPyContext *ctx, HPy);
                    return GraalHPyJNITrampolines.executeDebugHashfunc(receiver.pointer, convertHPyTraceContext(arguments), convertArgNode.execute(arguments, 1));
                case HPyFunc_binaryfunc:
                    return GraalHPyJNITrampolines.executeDebugBinaryfunc(receiver.pointer, convertHPyTraceContext(arguments), convertArgNode.execute(arguments, 1),
                                    convertArgNode.execute(arguments, 2));
                case HPyFunc_o:
                    return GraalHPyJNITrampolines.executeDebugO(receiver.pointer, convertHPyTraceContext(arguments), convertArgNode.execute(arguments, 1),
                                    convertArgNode.execute(arguments, 2));
                case HPyFunc_getattrofunc:
                    return GraalHPyJNITrampolines.executeDebugGetattrofunc(receiver.pointer, convertHPyTraceContext(arguments), convertArgNode.execute(arguments, 1),
                                    convertArgNode.execute(arguments, 2));
                case HPyFunc_getattrfunc:
                    // HPy (*HPyFunc_getattrfunc) (HPyContext *ctx, HPy, char *);
                    return GraalHPyJNITrampolines.executeDebugGetattrfunc(receiver.pointer, convertHPyTraceContext(arguments), convertArgNode.execute(arguments, 1),
                                    convertArgNode.execute(arguments, 2));
                case HPyFunc_ssizeargfunc:
                    // HPy (*HPyFunc_ssizeargfunc)(HPyContext *ctx, HPy, HPy_ssize_t);
                    return GraalHPyJNITrampolines.executeDebugSsizeargfunc(receiver.pointer, convertHPyTraceContext(arguments), convertArgNode.execute(arguments, 1),
                                    convertArgNode.execute(arguments, 2));
                case HPyFunc_getter:
                    // HPy (*HPyFunc_getter) (HPyContext *ctx, HPy, void *);
                    return GraalHPyJNITrampolines.executeDebugGetter(receiver.pointer, convertHPyTraceContext(arguments), convertArgNode.execute(arguments, 1),
                                    convertArgNode.execute(arguments, 2));
                case HPyFunc_traverseproc:
                    // int (*HPyFunc_traverseproc)(void *, HPyFunc_visitproc, void *);
                    return GraalHPyJNITrampolines.executeTraverseproc(receiver.pointer, convertHPyTraceContext(arguments), convertArgNode.execute(arguments, 1),
                                    convertArgNode.execute(arguments, 2));
                case HPyFunc_varargs:
                    // HPy (*HPyFunc_varargs)(HPyContext *, HPy, HPy *, HPy_ssize_t);
                    return GraalHPyJNITrampolines.executeDebugVarargs(receiver.pointer, convertHPyTraceContext(arguments), convertArgNode.execute(arguments, 1),
                                    convertArgNode.execute(arguments, 2), convertArgNode.execute(arguments, 3));
                case HPyFunc_ternaryfunc:
                    // HPy (*HPyFunc_ternaryfunc)(HPyContext *, HPy, HPy, HPy)
                    return GraalHPyJNITrampolines.executeDebugTernaryfunc(receiver.pointer, convertHPyTraceContext(arguments), convertArgNode.execute(arguments, 1),
                                    convertArgNode.execute(arguments, 2), convertArgNode.execute(arguments, 3));
                case HPyFunc_descrgetfunc:
                    return GraalHPyJNITrampolines.executeDebugDescrgetfunc(receiver.pointer, convertHPyTraceContext(arguments), convertArgNode.execute(arguments, 1),
                                    convertArgNode.execute(arguments, 2), convertArgNode.execute(arguments, 3));
                // HPy (*HPyFunc_descrgetfunc)(HPyContext *, HPy, HPy, HPy)
                case HPyFunc_ssizessizeargfunc:
                    // HPy (*HPyFunc_ssizessizeargfunc)(HPyContext *, HPy, HPy_ssize_t,
                    // HPy_ssize_t);
                    return GraalHPyJNITrampolines.executeDebugSsizessizeargfunc(receiver.pointer, convertHPyTraceContext(arguments), convertArgNode.execute(arguments, 1),
                                    convertArgNode.execute(arguments, 2), convertArgNode.execute(arguments, 3));
                case HPyFunc_keywords:
                    // HPy (*HPyFunc_keywords)(HPyContext *, HPy, HPy *, HPy_ssize_t , HPy)
                    return GraalHPyJNITrampolines.executeDebugKeywords(receiver.pointer, convertHPyTraceContext(arguments), convertArgNode.execute(arguments, 1),
                                    convertArgNode.execute(arguments, 2), convertArgNode.execute(arguments, 3), convertArgNode.execute(arguments, 4));
                case HPyFunc_inquiry:
                    return GraalHPyJNITrampolines.executeDebugInquiry(receiver.pointer, convertHPyTraceContext(arguments), convertArgNode.execute(arguments, 1));
                case HPyFunc_ssizeobjargproc:
                    return GraalHPyJNITrampolines.executeDebugSsizeobjargproc(receiver.pointer, convertHPyTraceContext(arguments), convertArgNode.execute(arguments, 1), (long) arguments[2],
                                    convertArgNode.execute(arguments, 3));
                case HPyFunc_initproc:
                    return GraalHPyJNITrampolines.executeDebugInitproc(receiver.pointer, convertHPyTraceContext(arguments), convertArgNode.execute(arguments, 1),
                                    convertArgNode.execute(arguments, 2), (long) arguments[3], convertArgNode.execute(arguments, 4));
                case HPyFunc_ssizessizeobjargproc:
                    return GraalHPyJNITrampolines.executeDebugSsizessizeobjargproc(receiver.pointer, convertHPyTraceContext(arguments), convertArgNode.execute(arguments, 1), (long) arguments[2],
                                    (long) arguments[3], convertArgNode.execute(arguments, 4));
                case HPyFunc_setter:
                    // int (*HPyFunc_setter)(HPyContext *ctx, HPy, HPy, void *);
                    return GraalHPyJNITrampolines.executeDebugSetter(receiver.pointer, convertHPyTraceContext(arguments), convertArgNode.execute(arguments, 1),
                                    convertArgNode.execute(arguments, 2), convertArgNode.execute(arguments, 3));
                case HPyFunc_setattrfunc:
                    // int (*HPyFunc_setattrfunc)(HPyContext *ctx, HPy, char *, HPy);
                    return GraalHPyJNITrampolines.executeDebugSetattrfunc(receiver.pointer, convertHPyTraceContext(arguments), convertArgNode.execute(arguments, 1),
                                    convertArgNode.execute(arguments, 2), convertArgNode.execute(arguments, 3));
                case HPyFunc_objobjargproc:
                    return GraalHPyJNITrampolines.executeDebugObjobjargproc(receiver.pointer, convertHPyTraceContext(arguments), convertArgNode.execute(arguments, 1),
                                    convertArgNode.execute(arguments, 2), convertArgNode.execute(arguments, 3));
                case HPyFunc_descrsetfunc:
                    return GraalHPyJNITrampolines.executeDebugDescrsetfunc(receiver.pointer, convertHPyTraceContext(arguments), convertArgNode.execute(arguments, 1),
                                    convertArgNode.execute(arguments, 2), convertArgNode.execute(arguments, 3));
                case HPyFunc_setattrofunc:
                    return GraalHPyJNITrampolines.executeDebugSetattrofunc(receiver.pointer, convertHPyTraceContext(arguments), convertArgNode.execute(arguments, 1),
                                    convertArgNode.execute(arguments, 2), convertArgNode.execute(arguments, 3));
                case HPyFunc_freefunc:
                    // no handles involved in freefunc; we can use the universal trampoline
                    GraalHPyJNITrampolines.executeDebugFreefunc(receiver.pointer, convertHPyTraceContext(arguments), convertArgNode.execute(arguments, 1));
                    return 0;
                case HPyFunc_richcmpfunc:
                    // HPy (*HPyFunc_richcmpfunc)(HPyContext *ctx, HPy, HPy, HPy_RichCmpOp)
                    return GraalHPyJNITrampolines.executeDebugRichcmpfunc(receiver.pointer, convertHPyTraceContext(arguments), convertArgNode.execute(arguments, 1),
                                    convertArgNode.execute(arguments, 2), (int) arguments[3]);
                case HPyFunc_objobjproc:
                    return GraalHPyJNITrampolines.executeDebugObjobjproc(receiver.pointer, convertHPyTraceContext(arguments), convertArgNode.execute(arguments, 1),
                                    convertArgNode.execute(arguments, 2));
                case HPyFunc_getbufferproc:
                    return GraalHPyJNITrampolines.executeDebugGetbufferproc(receiver.pointer, convertHPyTraceContext(arguments), convertArgNode.execute(arguments, 1),
                                    convertArgNode.execute(arguments, 2), (int) arguments[3]);
                case HPyFunc_releasebufferproc:
                    GraalHPyJNITrampolines.executeDebugReleasebufferproc(receiver.pointer, convertHPyTraceContext(arguments), convertArgNode.execute(arguments, 1),
                                    convertArgNode.execute(arguments, 2));
                    return 0;
                case HPyFunc_destroyfunc:
                    GraalHPyJNITrampolines.executeDestroyfunc(convertPointer(arguments[0], interopLibrary), receiver.pointer);
                    return 0;
                case HPyFunc_destructor:
                    GraalHPyJNITrampolines.executeDebugDestructor(receiver.pointer, convertHPyTraceContext(arguments), convertArgNode.execute(arguments, 1));
                    return 0;
            }
            throw CompilerDirectives.shouldNotReachHere();
        }

        /**
         * When we are in debug mode, we need to use different trampolines for calling the HPy
         * extension functions because object parameters (that will become handles) will be wrapped
         * in debug handles ({@code DHPy}) and, vice versa, object return values need to be
         * unwrapped. This un/-wrapping is done by the trampoline via calling {@code DHPy_open} and
         * {@code DHPy_unwrap}.
         */
        private static long callDebug(GraalHPyJNIFunctionPointer receiver, LLVMType signature, long ctx, Object[] arguments,
                        InteropLibrary interopLibrary, GraalHPyJNIConvertArgNode convertArgNode) {
            switch (signature) {
                case HPyModule_init:
                    // there is not difference to the universal mode
                    return GraalHPyJNITrampolines.executeModuleInit(receiver.pointer);
                case HPyModule_create:
                    return GraalHPyJNITrampolines.executeDebugModcreate(receiver.pointer, ctx, convertArgNode.execute(arguments, 1));
                case HPyFunc_noargs:
                    return GraalHPyJNITrampolines.executeDebugNoargs(receiver.pointer, ctx, convertArgNode.execute(arguments, 1));
                case HPyFunc_unaryfunc:
                    return GraalHPyJNITrampolines.executeDebugUnaryfunc(receiver.pointer, ctx, convertArgNode.execute(arguments, 1));
                case HPyFunc_getiterfunc:
                    return GraalHPyJNITrampolines.executeDebugGetiterfunc(receiver.pointer, ctx, convertArgNode.execute(arguments, 1));
                case HPyFunc_iternextfunc:
                    return GraalHPyJNITrampolines.executeDebugIternextfunc(receiver.pointer, ctx, convertArgNode.execute(arguments, 1));
                case HPyFunc_reprfunc:
                    return GraalHPyJNITrampolines.executeDebugReprfunc(receiver.pointer, ctx, convertArgNode.execute(arguments, 1));
                case HPyFunc_lenfunc:
                    return GraalHPyJNITrampolines.executeDebugLenfunc(receiver.pointer, ctx, convertArgNode.execute(arguments, 1));
                case HPyFunc_hashfunc:
                    // HPy_ssize_t (*HPyFunc_lenfunc)(HPyContext *ctx, HPy);
                    // HPy_hash_t (*HPyFunc_hashfunc)(HPyContext *ctx, HPy);
                    return GraalHPyJNITrampolines.executeDebugHashfunc(receiver.pointer, ctx, convertArgNode.execute(arguments, 1));
                case HPyFunc_binaryfunc:
                    return GraalHPyJNITrampolines.executeDebugBinaryfunc(receiver.pointer, ctx, convertArgNode.execute(arguments, 1),
                                    convertArgNode.execute(arguments, 2));
                case HPyFunc_o:
                    return GraalHPyJNITrampolines.executeDebugO(receiver.pointer, ctx, convertArgNode.execute(arguments, 1),
                                    convertArgNode.execute(arguments, 2));
                case HPyFunc_getattrofunc:
                    return GraalHPyJNITrampolines.executeDebugGetattrofunc(receiver.pointer, ctx, convertArgNode.execute(arguments, 1),
                                    convertArgNode.execute(arguments, 2));
                case HPyFunc_getattrfunc:
                    // HPy (*HPyFunc_getattrfunc) (HPyContext *ctx, HPy, char *);
                    return GraalHPyJNITrampolines.executeDebugGetattrfunc(receiver.pointer, ctx, convertArgNode.execute(arguments, 1),
                                    convertArgNode.execute(arguments, 2));
                case HPyFunc_ssizeargfunc:
                    // HPy (*HPyFunc_ssizeargfunc)(HPyContext *ctx, HPy, HPy_ssize_t);
                    return GraalHPyJNITrampolines.executeDebugSsizeargfunc(receiver.pointer, ctx, convertArgNode.execute(arguments, 1),
                                    convertArgNode.execute(arguments, 2));
                case HPyFunc_getter:
                    // HPy (*HPyFunc_getter) (HPyContext *ctx, HPy, void *);
                    return GraalHPyJNITrampolines.executeDebugGetter(receiver.pointer, ctx, convertArgNode.execute(arguments, 1),
                                    convertArgNode.execute(arguments, 2));
                case HPyFunc_traverseproc:
                    // int (*HPyFunc_traverseproc)(void *, HPyFunc_visitproc, void *);
                    return GraalHPyJNITrampolines.executeTraverseproc(receiver.pointer, ctx, convertArgNode.execute(arguments, 1),
                                    convertArgNode.execute(arguments, 2));
                case HPyFunc_varargs:
                    // HPy (*HPyFunc_varargs)(HPyContext *, HPy, HPy *, HPy_ssize_t);
                    return GraalHPyJNITrampolines.executeDebugVarargs(receiver.pointer, ctx, convertArgNode.execute(arguments, 1),
                                    convertArgNode.execute(arguments, 2), convertArgNode.execute(arguments, 3));
                case HPyFunc_ternaryfunc:
                    // HPy (*HPyFunc_ternaryfunc)(HPyContext *, HPy, HPy, HPy)
                    return GraalHPyJNITrampolines.executeDebugTernaryfunc(receiver.pointer, ctx, convertArgNode.execute(arguments, 1),
                                    convertArgNode.execute(arguments, 2), convertArgNode.execute(arguments, 3));
                case HPyFunc_descrgetfunc:
                    return GraalHPyJNITrampolines.executeDebugDescrgetfunc(receiver.pointer, ctx, convertArgNode.execute(arguments, 1),
                                    convertArgNode.execute(arguments, 2), convertArgNode.execute(arguments, 3));
                // HPy (*HPyFunc_descrgetfunc)(HPyContext *, HPy, HPy, HPy)
                case HPyFunc_ssizessizeargfunc:
                    // HPy (*HPyFunc_ssizessizeargfunc)(HPyContext *, HPy, HPy_ssize_t,
                    // HPy_ssize_t);
                    return GraalHPyJNITrampolines.executeDebugSsizessizeargfunc(receiver.pointer, ctx, convertArgNode.execute(arguments, 1),
                                    convertArgNode.execute(arguments, 2), convertArgNode.execute(arguments, 3));
                case HPyFunc_keywords:
                    // HPy (*HPyFunc_keywords)(HPyContext *, HPy, HPy *, HPy_ssize_t , HPy)
                    return GraalHPyJNITrampolines.executeDebugKeywords(receiver.pointer, ctx, convertArgNode.execute(arguments, 1),
                                    convertArgNode.execute(arguments, 2), convertArgNode.execute(arguments, 3), convertArgNode.execute(arguments, 4));
                case HPyFunc_inquiry:
                    return GraalHPyJNITrampolines.executeDebugInquiry(receiver.pointer, ctx, convertArgNode.execute(arguments, 1));
                case HPyFunc_ssizeobjargproc:
                    return GraalHPyJNITrampolines.executeDebugSsizeobjargproc(receiver.pointer, ctx, convertArgNode.execute(arguments, 1), (long) arguments[2],
                                    convertArgNode.execute(arguments, 3));
                case HPyFunc_initproc:
                    return GraalHPyJNITrampolines.executeDebugInitproc(receiver.pointer, ctx, convertArgNode.execute(arguments, 1),
                                    convertArgNode.execute(arguments, 2), (long) arguments[3], convertArgNode.execute(arguments, 4));
                case HPyFunc_ssizessizeobjargproc:
                    return GraalHPyJNITrampolines.executeDebugSsizessizeobjargproc(receiver.pointer, ctx, convertArgNode.execute(arguments, 1), (long) arguments[2],
                                    (long) arguments[3], convertArgNode.execute(arguments, 4));
                case HPyFunc_setter:
                    // int (*HPyFunc_setter)(HPyContext *ctx, HPy, HPy, void *);
                    return GraalHPyJNITrampolines.executeDebugSetter(receiver.pointer, ctx, convertArgNode.execute(arguments, 1),
                                    convertArgNode.execute(arguments, 2), convertArgNode.execute(arguments, 3));
                case HPyFunc_setattrfunc:
                    // int (*HPyFunc_setattrfunc)(HPyContext *ctx, HPy, char *, HPy);
                    return GraalHPyJNITrampolines.executeDebugSetattrfunc(receiver.pointer, ctx, convertArgNode.execute(arguments, 1),
                                    convertArgNode.execute(arguments, 2), convertArgNode.execute(arguments, 3));
                case HPyFunc_objobjargproc:
                    return GraalHPyJNITrampolines.executeDebugObjobjargproc(receiver.pointer, ctx, convertArgNode.execute(arguments, 1),
                                    convertArgNode.execute(arguments, 2), convertArgNode.execute(arguments, 3));
                case HPyFunc_descrsetfunc:
                    return GraalHPyJNITrampolines.executeDebugDescrsetfunc(receiver.pointer, ctx, convertArgNode.execute(arguments, 1),
                                    convertArgNode.execute(arguments, 2), convertArgNode.execute(arguments, 3));
                case HPyFunc_setattrofunc:
                    return GraalHPyJNITrampolines.executeDebugSetattrofunc(receiver.pointer, ctx, convertArgNode.execute(arguments, 1),
                                    convertArgNode.execute(arguments, 2), convertArgNode.execute(arguments, 3));
                case HPyFunc_freefunc:
                    // no handles involved in freefunc; we can use the universal trampoline
                    GraalHPyJNITrampolines.executeDebugFreefunc(receiver.pointer, ctx, convertArgNode.execute(arguments, 1));
                    return 0;
                case HPyFunc_richcmpfunc:
                    // HPy (*HPyFunc_richcmpfunc)(HPyContext *ctx, HPy, HPy, HPy_RichCmpOp)
                    return GraalHPyJNITrampolines.executeDebugRichcmpfunc(receiver.pointer, ctx, convertArgNode.execute(arguments, 1),
                                    convertArgNode.execute(arguments, 2), (int) arguments[3]);
                case HPyFunc_objobjproc:
                    return GraalHPyJNITrampolines.executeDebugObjobjproc(receiver.pointer, ctx, convertArgNode.execute(arguments, 1),
                                    convertArgNode.execute(arguments, 2));
                case HPyFunc_getbufferproc:
                    return GraalHPyJNITrampolines.executeDebugGetbufferproc(receiver.pointer, ctx, convertArgNode.execute(arguments, 1),
                                    convertArgNode.execute(arguments, 2), (int) arguments[3]);
                case HPyFunc_releasebufferproc:
                    GraalHPyJNITrampolines.executeDebugReleasebufferproc(receiver.pointer, ctx, convertArgNode.execute(arguments, 1),
                                    convertArgNode.execute(arguments, 2));
                    return 0;
                case HPyFunc_destroyfunc:
                    GraalHPyJNITrampolines.executeDestroyfunc(convertPointer(arguments[0], interopLibrary), receiver.pointer);
                    return 0;
                case HPyFunc_destructor:
                    GraalHPyJNITrampolines.executeDebugDestructor(receiver.pointer, ctx, convertArgNode.execute(arguments, 1));
                    return 0;
            }
            throw CompilerDirectives.shouldNotReachHere();
        }

        private static long convertHPyContext(Object[] arguments) {
            GraalHPyJNIContext jniBackend = GraalHPyJNIConvertArgNode.getHPyContext(arguments);
            try {
                return jniBackend.asPointer();
            } catch (UnsupportedMessageException e) {
                throw CompilerDirectives.shouldNotReachHere();
            }
        }

        private static long convertHPyDebugContext(Object[] arguments) {
            GraalHPyJNIContext jniBackend = GraalHPyJNIConvertArgNode.getHPyContext(arguments);
            assert jniBackend.getHPyDebugContext() != 0;
            return jniBackend.getHPyDebugContext();
        }

        private static long convertHPyTraceContext(Object[] arguments) {
            GraalHPyJNIContext jniBackend = GraalHPyJNIConvertArgNode.getHPyContext(arguments);
            assert jniBackend.getHPyTraceContext() != 0;
            return jniBackend.getHPyTraceContext();
        }

        private static long convertPointer(Object argument, InteropLibrary interopLibrary) {
            if (!interopLibrary.isPointer(argument)) {
                interopLibrary.toNative(argument);
            }
            try {
                return interopLibrary.asPointer(argument);
            } catch (UnsupportedMessageException e) {
                throw CompilerDirectives.shouldNotReachHere();
            }
        }
    }

    @ExportMessage
    @SuppressWarnings("static-method")
    boolean isPointer() {
        return true;
    }

    @ExportMessage
    long asPointer() {
        return pointer;
    }
}
