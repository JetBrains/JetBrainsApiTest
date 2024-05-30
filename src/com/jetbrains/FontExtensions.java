/*
 * Copyright 2000-2023 JetBrains s.r.o.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package com.jetbrains;

import java.awt.*;
import java.util.*;

/**
 * Font-related utilities.
 */
@Service
@Provided
public interface FontExtensions {
    /**
     * Magic constant representing enabled feature.
     * @see Features
     */
    public static final int FEATURE_ON = 1;
    /**
     * Magic constant representing disabled feature.
     * @see Features
     */
    public static final int FEATURE_OFF = 0;

    /**
     * The list of all supported features. For feature's description look at
     * <a href=https://learn.microsoft.com/en-us/typography/opentype/spec/featurelist>documentation</a> <br>
     * The following features: KERN, LIGA, CALT are missing intentionally. These features will be added automatically
     * by adding {@link java.awt.font.TextAttribute} to {@link java.awt.Font}:
     * <ul>
     * <li>Attribute {@link java.awt.font.TextAttribute#KERNING} manages KERN feature</li>
     * <li>Attribute {@link java.awt.font.TextAttribute#LIGATURES} manages LIGA and CALT features</li>
     * </ul>
     */
    enum FeatureTag {
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#aalt>aalt</a>*/ AALT,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#abvf>abvf</a>*/ ABVF,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#abvm>abvm</a>*/ ABVM,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#abvs>abvs</a>*/ ABVS,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#afrc>afrc</a>*/ AFRC,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#akhn>akhn</a>*/ AKHN,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#blwf>blwf</a>*/ BLWF,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#blwm>blwm</a>*/ BLWM,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#blws>blws</a>*/ BLWS,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#case>case</a>*/ CASE,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#ccmp>ccmp</a>*/ CCMP,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cfar>cfar</a>*/ CFAR,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#chws>chws</a>*/ CHWS,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cjct>cjct</a>*/ CJCT,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#clig>clig</a>*/ CLIG,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cpct>cpct</a>*/ CPCT,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cpsp>cpsp</a>*/ CPSP,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cswh>cswh</a>*/ CSWH,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#curs>curs</a>*/ CURS,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv01>cv01</a>*/ CV01,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv02>cv02</a>*/ CV02,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv03>cv03</a>*/ CV03,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv04>cv04</a>*/ CV04,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv05>cv05</a>*/ CV05,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv06>cv06</a>*/ CV06,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv07>cv07</a>*/ CV07,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv08>cv08</a>*/ CV08,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv09>cv09</a>*/ CV09,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv10>cv10</a>*/ CV10,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv11>cv11</a>*/ CV11,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv12>cv12</a>*/ CV12,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv13>cv13</a>*/ CV13,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv14>cv14</a>*/ CV14,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv15>cv15</a>*/ CV15,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv16>cv16</a>*/ CV16,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv17>cv17</a>*/ CV17,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv18>cv18</a>*/ CV18,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv19>cv19</a>*/ CV19,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv20>cv20</a>*/ CV20,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv21>cv21</a>*/ CV21,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv22>cv22</a>*/ CV22,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv23>cv23</a>*/ CV23,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv24>cv24</a>*/ CV24,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv25>cv25</a>*/ CV25,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv26>cv26</a>*/ CV26,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv27>cv27</a>*/ CV27,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv28>cv28</a>*/ CV28,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv29>cv29</a>*/ CV29,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv30>cv30</a>*/ CV30,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv31>cv31</a>*/ CV31,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv32>cv32</a>*/ CV32,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv33>cv33</a>*/ CV33,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv34>cv34</a>*/ CV34,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv35>cv35</a>*/ CV35,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv36>cv36</a>*/ CV36,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv37>cv37</a>*/ CV37,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv38>cv38</a>*/ CV38,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv39>cv39</a>*/ CV39,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv40>cv40</a>*/ CV40,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv41>cv41</a>*/ CV41,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv42>cv42</a>*/ CV42,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv43>cv43</a>*/ CV43,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv44>cv44</a>*/ CV44,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv45>cv45</a>*/ CV45,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv46>cv46</a>*/ CV46,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv47>cv47</a>*/ CV47,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv48>cv48</a>*/ CV48,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv49>cv49</a>*/ CV49,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv50>cv50</a>*/ CV50,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv51>cv51</a>*/ CV51,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv52>cv52</a>*/ CV52,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv53>cv53</a>*/ CV53,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv54>cv54</a>*/ CV54,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv55>cv55</a>*/ CV55,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv56>cv56</a>*/ CV56,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv57>cv57</a>*/ CV57,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv58>cv58</a>*/ CV58,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv59>cv59</a>*/ CV59,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv60>cv60</a>*/ CV60,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv61>cv61</a>*/ CV61,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv62>cv62</a>*/ CV62,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv63>cv63</a>*/ CV63,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv64>cv64</a>*/ CV64,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv65>cv65</a>*/ CV65,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv66>cv66</a>*/ CV66,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv67>cv67</a>*/ CV67,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv68>cv68</a>*/ CV68,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv69>cv69</a>*/ CV69,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv70>cv70</a>*/ CV70,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv71>cv71</a>*/ CV71,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv72>cv72</a>*/ CV72,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv73>cv73</a>*/ CV73,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv74>cv74</a>*/ CV74,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv75>cv75</a>*/ CV75,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv76>cv76</a>*/ CV76,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv77>cv77</a>*/ CV77,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv78>cv78</a>*/ CV78,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv79>cv79</a>*/ CV79,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv80>cv80</a>*/ CV80,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv81>cv81</a>*/ CV81,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv82>cv82</a>*/ CV82,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv83>cv83</a>*/ CV83,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv84>cv84</a>*/ CV84,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv85>cv85</a>*/ CV85,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv86>cv86</a>*/ CV86,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv87>cv87</a>*/ CV87,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv88>cv88</a>*/ CV88,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv89>cv89</a>*/ CV89,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv90>cv90</a>*/ CV90,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv91>cv91</a>*/ CV91,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv92>cv92</a>*/ CV92,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv93>cv93</a>*/ CV93,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv94>cv94</a>*/ CV94,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv95>cv95</a>*/ CV95,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv96>cv96</a>*/ CV96,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv97>cv97</a>*/ CV97,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv98>cv98</a>*/ CV98,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#cv99>cv99</a>*/ CV99,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#c2pc>c2pc</a>*/ C2PC,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#c2sc>c2sc</a>*/ C2SC,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#dist>dist</a>*/ DIST,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#dlig>dlig</a>*/ DLIG,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#dnom>dnom</a>*/ DNOM,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#dtls>dtls</a>*/ DTLS,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ae#expt>expt</a>*/ EXPT,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_fj#falt>falt</a>*/ FALT,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_fj#fin2>fin2</a>*/ FIN2,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_fj#fin3>fin3</a>*/ FIN3,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_fj#fina>fina</a>*/ FINA,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_fj#flac>flac</a>*/ FLAC,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_fj#frac>frac</a>*/ FRAC,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_fj#fwid>fwid</a>*/ FWID,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_fj#half>half</a>*/ HALF,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_fj#haln>haln</a>*/ HALN,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_fj#halt>halt</a>*/ HALT,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_fj#hist>hist</a>*/ HIST,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_fj#hkna>hkna</a>*/ HKNA,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_fj#hlig>hlig</a>*/ HLIG,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_fj#hngl>hngl</a>*/ HNGL,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_fj#hojo>hojo</a>*/ HOJO,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_fj#hwid>hwid</a>*/ HWID,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_fj#init>init</a>*/ INIT,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_fj#isol>isol</a>*/ ISOL,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_fj#ital>ital</a>*/ ITAL,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_fj#jalt>jalt</a>*/ JALT,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_fj#jp78>jp78</a>*/ JP78,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_fj#jp83>jp83</a>*/ JP83,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_fj#jp90>jp90</a>*/ JP90,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_fj#jp04>jp04</a>*/ JP04,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ko#lfbd>lfbd</a>*/ LFBD,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ko#ljmo>ljmo</a>*/ LJMO,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ko#lnum>lnum</a>*/ LNUM,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ko#locl>locl</a>*/ LOCL,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ko#ltra>ltra</a>*/ LTRA,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ko#ltrm>ltrm</a>*/ LTRM,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ko#mark>mark</a>*/ MARK,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ko#med2>med2</a>*/ MED2,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ko#medi>medi</a>*/ MEDI,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ko#mgrk>mgrk</a>*/ MGRK,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ko#mkmk>mkmk</a>*/ MKMK,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ko#mset>mset</a>*/ MSET,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ko#nalt>nalt</a>*/ NALT,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ko#nlck>nlck</a>*/ NLCK,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ko#nukt>nukt</a>*/ NUKT,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ko#numr>numr</a>*/ NUMR,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ko#onum>onum</a>*/ ONUM,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ko#opbd>opbd</a>*/ OPBD,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ko#ordn>ordn</a>*/ ORDN,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_ko#ornm>ornm</a>*/ ORNM,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_pt#palt>palt</a>*/ PALT,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_pt#pcap>pcap</a>*/ PCAP,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_pt#pkna>pkna</a>*/ PKNA,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_pt#pnum>pnum</a>*/ PNUM,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_pt#pref>pref</a>*/ PREF,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_pt#pres>pres</a>*/ PRES,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_pt#pstf>pstf</a>*/ PSTF,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_pt#psts>psts</a>*/ PSTS,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_pt#pwid>pwid</a>*/ PWID,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_pt#qwid>qwid</a>*/ QWID,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_pt#rand>rand</a>*/ RAND,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_pt#rclt>rclt</a>*/ RCLT,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_pt#rkrf>rkrf</a>*/ RKRF,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_pt#rlig>rlig</a>*/ RLIG,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_pt#rphf>rphf</a>*/ RPHF,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_pt#rtbd>rtbd</a>*/ RTBD,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_pt#rtla>rtla</a>*/ RTLA,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_pt#rtlm>rtlm</a>*/ RTLM,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_pt#ruby>ruby</a>*/ RUBY,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_pt#rvrn>rvrn</a>*/ RVRN,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_pt#salt>salt</a>*/ SALT,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_pt#sinf>sinf</a>*/ SINF,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_pt#size>size</a>*/ SIZE,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_pt#smcp>smcp</a>*/ SMCP,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_pt#smpl>smpl</a>*/ SMPL,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_pt#ss01>ss01</a>*/ SS01,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_pt#ss02>ss02</a>*/ SS02,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_pt#ss03>ss03</a>*/ SS03,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_pt#ss04>ss04</a>*/ SS04,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_pt#ss05>ss05</a>*/ SS05,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_pt#ss06>ss06</a>*/ SS06,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_pt#ss07>ss07</a>*/ SS07,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_pt#ss08>ss08</a>*/ SS08,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_pt#ss09>ss09</a>*/ SS09,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_pt#ss10>ss10</a>*/ SS10,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_pt#ss11>ss11</a>*/ SS11,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_pt#ss12>ss12</a>*/ SS12,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_pt#ss13>ss13</a>*/ SS13,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_pt#ss14>ss14</a>*/ SS14,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_pt#ss15>ss15</a>*/ SS15,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_pt#ss16>ss16</a>*/ SS16,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_pt#ss17>ss17</a>*/ SS17,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_pt#ss18>ss18</a>*/ SS18,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_pt#ss19>ss19</a>*/ SS19,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_pt#ss20>ss20</a>*/ SS20,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_pt#ssty>ssty</a>*/ SSTY,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_pt#stch>stch</a>*/ STCH,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_pt#subs>subs</a>*/ SUBS,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_pt#sups>sups</a>*/ SUPS,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_pt#swsh>swsh</a>*/ SWSH,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_pt#titl>titl</a>*/ TITL,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_pt#tjmo>tjmo</a>*/ TJMO,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_pt#tnam>tnam</a>*/ TNAM,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_pt#tnum>tnum</a>*/ TNUM,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_pt#trad>trad</a>*/ TRAD,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_pt#twid>twid</a>*/ TWID,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_uz#unic>unic</a>*/ UNIC,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_uz#valt>valt</a>*/ VALT,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_uz#vatu>vatu</a>*/ VATU,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_uz#vchw>vchw</a>*/ VCHW,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_uz#vert>vert</a>*/ VERT,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_uz#vhal>vhal</a>*/ VHAL,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_uz#vjmo>vjmo</a>*/ VJMO,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_uz#vkna>vkna</a>*/ VKNA,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_uz#vkrn>vkrn</a>*/ VKRN,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_uz#vpal>vpal</a>*/ VPAL,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_uz#vrt2>vrt2</a>*/ VRT2,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_uz#vrtr>vrtr</a>*/ VRTR,
        /**<a href=https://learn.microsoft.com/en-us/typography/opentype/spec/features_uz#zero>zero</a>*/ ZERO;

        /**
         * Get feature name in lower case.
         * @return feature name
         */
        String getName() {
            return toString().toLowerCase();
        }

        /**
         * Get feature by case-insensitive name.
         * @param str feature name (case-insensitive)
         * @return feature tag
         */
        public static Optional<FeatureTag> getFeatureTag(String str) {
            try {
                return Optional.of(FeatureTag.valueOf(str.toUpperCase()));
            } catch (IllegalArgumentException ignored) {
                return Optional.empty();
            }
        }
    }

    /**
     * Map of feature tags with corresponding {@linkplain #FEATURE_ON hints}.
     */
    @Provides
    final class Features extends TreeMap<FeatureTag, Integer> {
        private static final long serialVersionUID = 1L;

        /**
         * Copy constructor.
         * @param map map to copy from
         */
        public Features(Map<FeatureTag, Integer> map) {
            super(map);
        }

        /**
         * Constructs a map from variable number of features,
         * with {@link #FEATURE_ON} value for each feature tag.
         * @param features feature tags to enable
         */
        public Features(FeatureTag... features) {
            Arrays.stream(features).forEach(tag -> put(tag, FontExtensions.FEATURE_ON));
        }

        private TreeMap<String, Integer> getAsTreeMap() {
            TreeMap<String, Integer> res = new TreeMap<>();
            forEach((tag, value) -> res.put(tag.getName(), value));
            return res;
        }
    }

    /**
     * This method derives a new {@link java.awt.Font} object with certain set of {@link FeatureTag}
     * and corresponding values.
     *
     * @param font       basic font
     * @param features   set of OpenType's features wrapped inside {@link Features}
     * @return new font
     */
    Font deriveFontWithFeatures(Font font, Features features);

    /**
     * This method returns set of OpenType features converted to String supported by the current font
     *
     * @param font       basic font
     */
    Set<String> getAvailableFeatures(Font font);

    /**
     * Get subpixel resolution for rendering text with greyscale antialiasing,
     * set with {@code -Djava2d.font.subpixelResolution=NxM}, with integers
     * between 1 and 16 instead of N and M.
     * This only affects text rendered via glyph cache. Value of NxM means
     * that each glyph has rasterized images for N distinct positions horizontally
     * and M positions vertically. This effectively increases quality of glyph
     * spacing in each direction at the cost of N*M times increased memory consumption.
     * @return subpixel resolution (N, M)
     */
    Dimension getSubpixelResolution();
}